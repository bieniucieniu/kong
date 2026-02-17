package com.bieniucieniu.routing


import com.bieniucieniu.errors.auth.UnauthorizedException
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleMemoryCache.memoryCache
import io.github.flaxoos.ktor.server.plugins.ratelimiter.RateLimiting
import io.github.flaxoos.ktor.server.plugins.ratelimiter.implementations.TokenBucket
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
    val dev: Boolean? by lazy { environment.config.propertyOrNull("ktor.development")?.getAs() }
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
        fun buildInnerCause(e: Throwable): List<String> {
            val reason = mutableListOf<String>()
            var innerCause = e.cause
            while (innerCause != null) {
                reason.add(e.message ?: "Unknown error")
                innerCause = innerCause.cause
            }
            return reason
        }

        exception<Throwable> { call, cause ->
            when (cause) {
                is UnauthorizedException -> {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(cause.message ?: "Unauthorized", reason = buildInnerCause(cause))
                    )
                }

                else -> call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(cause.message ?: "Unknown error", reason = buildInnerCause(cause))
                )
            }

        }
    }
    install(Compression)

    if (environment.config.propertyOrNull("ktor.proxy")?.getAs<Boolean>() == true) {
        install(ForwardedHeaders)
        install(XForwardedHeaders)
    }
    install(SimpleCache) {
        memoryCache {
            invalidateAt = 10.seconds
        }
    }

    routing {
        if (dev == true) {
            swaggerUI(path = "swagger") {
                info = OpenApiInfo(title = "My API", version = "1.0.0")
                source = OpenApiDocSource.FirstOf(
                    OpenApiDocSource.Routing(contentType = ContentType.Application.Yaml) {
                        // filter out all wildcard matching to fix schema gen on frontend (orval)
                        routingRoot.descendants().filterNot { it.path.contains("...}") }
                    },
                )
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


        route("/") {
            install(RateLimiting) {
                rateLimiter {
                    type = TokenBucket::class
                    capacity = 100
                    rate = 10.seconds
                }
            }
        }
    }
}