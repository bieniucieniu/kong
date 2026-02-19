package com.bieniucieniu.routing


import com.bieniucieniu.errors.responses.ResponsesException
import com.bieniucieniu.errors.responses.ResponsesExceptionWithContent
import com.bieniucieniu.errors.responses.UnauthorizedException
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleMemoryCache.memoryCache
import io.github.flaxoos.ktor.server.plugins.ratelimiter.RateLimiting
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.openapi.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds


fun Application.installRoutingPlugins() {
    val config = environment.config
    val dev: Boolean by lazy { config.propertyOrNull("ktor.development")?.getAs() ?: false }
    val proxy: Boolean by lazy { config.propertyOrNull("ktor.proxy")?.getAs() ?: false }
    val jsonConfig: Json by inject()
    install(ContentNegotiation) {
        json(jsonConfig)
        json(jsonConfig, ContentType.Application.FormUrlEncoded)
    }
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
    install(StatusPages) {
        fun collectInnerCauses(e: Throwable): List<String> {
            var e = e.cause

            val reason = mutableListOf<String>()
            while (e != null) {
                reason.add(e.message ?: "Unknown error")
                e = e.cause
            }
            return reason
        }

        exception<Throwable> { call, cause ->
            print(cause)
            cause.printStackTrace()
            when (cause) {
                is UnauthorizedException -> {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(cause.message ?: "Unauthorized", reason = collectInnerCauses(cause))
                    )
                }

                is ResponsesException -> {
                    call.respond(
                        cause.status,
                        ErrorResponse(cause.message ?: "Unknown error", reason = collectInnerCauses(cause))
                    )
                }

                is ResponsesExceptionWithContent -> {
                    call.respond(
                        cause.status,
                        cause.content,
                        cause.typeInfo,
                    )
                }

                else -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(cause.message ?: "Unknown error", reason = collectInnerCauses(cause))
                )
            }

        }
    }
    install(Compression)

    if (proxy) {
        install(ForwardedHeaders)
        install(XForwardedHeaders)
    }
    install(SimpleCache) {
        memoryCache {
            invalidateAt = 10.seconds
        }
    }

    routing {
        if (dev) {
            swaggerUI(path = "swagger") {
                info = OpenApiInfo(title = "My API", version = "1.0.0")
                source = OpenApiDocSource.Routing(contentType = ContentType.Application.Yaml) {
                    // filter out all wildcard matching to fix schema gen on frontend (orval)
                    routingRoot.descendants().filterNot { it.path.contains("...}") }
                }

            }
        }

        staticResources("/", "frontend").describe {
            description = "static frontend resources"
            responses {
                HttpStatusCode.OK {
                    ContentType.Text.Html()
                }
                HttpStatusCode.NotFound {
                }
            }
        }


        install(RateLimiting) {
            rateLimiter {
                capacity = 100
                rate = 10.seconds
            }
        }
    }
}