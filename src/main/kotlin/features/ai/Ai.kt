package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import com.bieniucieniu.auth.authenticateUserSession
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import com.bieniucieniu.features.ai.routes.chatRoutes
import com.bieniucieniu.features.ai.routes.modelProviderRoutes
import com.bieniucieniu.features.ai.routes.modelRoutes
import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes


fun Application.configureAi() {
    val ollamaService: OllamaService by inject()
    install(Koog) {
        val config = environment.config
        llm {
            config.propertyOrNull("ai.ollama.baseUrl")?.getString().also { url ->
                ollama {
                    baseUrl = url
                    httpClient = get(named("ollama-http-client"))
                }.also {
                    config.propertyOrNull("ai.ollama.models")?.getList()
                        .takeIf { it.isNullOrEmpty() }
                        ?.also { launch { ollamaService.ensureInstalledModels(it) } }
                }
            }


            config.propertyOrNull("ai.google.apikey")?.getString()?.also {
                google(it) {
                    httpClient = get()
                }
            }
        }
    }

    routing {
        route("/api/ai") {
            authenticateUserSession {
                chatRoutes()
            }
            cacheOutput(30.minutes) {
                modelProviderRoutes()
                modelRoutes()
            }
        }
    }
}

