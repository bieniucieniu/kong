package com.bieniucieniu.features.ai

import ai.koog.ktor.Koog
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureAi() {
    install(Koog) {

    }
    routing {
        aiRoutes()
    }
}