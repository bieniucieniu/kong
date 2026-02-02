package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureAi() {
    install(Koog) {
        llm {
            ollama {
                this.httpClient = HttpClient(CIO) {
                    install(Auth) {
                        basic {
                            credentials {
                                BasicAuthCredentials(username = "jetbrains", password = "foobar")
                            }
                            realm = "jetbrains.com"
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