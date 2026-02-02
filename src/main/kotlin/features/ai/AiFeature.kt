package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import io.ktor.client.*
import io.ktor.client.engine.cio.*
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
                httpClient = HttpClient(CIO) {
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