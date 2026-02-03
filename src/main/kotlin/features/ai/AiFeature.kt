package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.logging.*
import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.configureAi() {
    install(Koog) {
        llm {
            ollama {
                val username = environment.config.property("koog.ollama.username").getString()
                val password = environment.config.property("koog.ollama.password").getString()
                baseUrl = environment.config.property("koog.ollama.baseUrl").getString()
                val baseUrlRealm = environment.config.propertyOrNull("koog.ollama.baseUrlRealm")?.getString()
                httpClient = HttpClient(CIO) {
                    install(HttpTimeout) {
                        requestTimeoutMillis = null
                        connectTimeoutMillis = 60_000
                        socketTimeoutMillis = 60_000
                    }
                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = LogLevel.ALL
                    }
                    install(Auth) {
                        basic {
                            credentials {
                                BasicAuthCredentials(
                                    username = username,
                                    password = password
                                )
                            }
                            if (!baseUrlRealm.isNullOrBlank()) realm = baseUrlRealm
                        }
                    }
                }
            }
        }
    }
    routing {
        aiRoutes()
    }
}