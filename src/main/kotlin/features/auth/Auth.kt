package com.bieniucieniu.features.auth

import com.bieniucieniu.features.auth.routes.authRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.configureAuth() {
    routing {
        route("api/auth") {
            authRoutes()
        }
    }
}