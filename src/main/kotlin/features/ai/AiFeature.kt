package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureAi() {
    install(Koog) {
        llm {
            val client: HttpClient by inject()
            val ollamaBaseUrl = environment.config.propertyOrNull("koog.ollama.baseUrl")?.getString()
            if (!ollamaBaseUrl.isNullOrBlank()) {
                ollama {
                    baseUrl = ollamaBaseUrl
                    httpClient = client
                }
            }
        }
    }
    routing {
        aiRoutes()
    }
}