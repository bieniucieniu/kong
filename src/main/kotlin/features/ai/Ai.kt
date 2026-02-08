package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import com.bieniucieniu.features.ai.routes.aiRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get


fun Application.configureAi() {
    install(Koog) {
        llm {

            val ollamaBaseUrl = environment.config.propertyOrNull("koog.ollama.baseUrl")?.getString()
            if (!ollamaBaseUrl.isNullOrBlank()) {
                ollama {
                    baseUrl = ollamaBaseUrl
                    httpClient = get(named("ollama-http-client"))
                }
            }
            environment.config.propertyOrNull("ai.google.apikey")?.getString()?.also {
                google(it) {
                    httpClient = get()
                }
            }
        }
    }
    routing {
        route("/api/ai") {
            aiRoutes()
        }
    }
}