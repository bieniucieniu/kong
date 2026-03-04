package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import com.bieniucieniu.auth.authenticateUserSession
import com.bieniucieniu.di.modules.OLLAMA_HTTP_CLIENT_QUALIFIER
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import com.bieniucieniu.features.ai.routes.*
import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
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
                    httpClient = get(OLLAMA_HTTP_CLIENT_QUALIFIER)
                }.also {
                    config.propertyOrNull("ai.ollama.models")?.getList()
                        .takeIf { !it.isNullOrEmpty() }
                        ?.let {
                            print("Installing models ${it.joinToString()}")
                            launch { ollamaService.ensureInstalledModels(it) }
                        }
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
            freeChatRoutes()

            authenticateUserSession {
                chatRoutes()
                sessionRoutes()
            }
            cacheOutput(30.minutes) {
                modelProviderRoutes()
                modelRoutes()
            }
        }
    }
}

