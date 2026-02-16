package com.bieniucieniu.features.auth

import com.bieniucieniu.auth.authenticateUserSession
import com.bieniucieniu.features.auth.routes.authRoutes
import com.bieniucieniu.features.auth.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.configureAuth() {
    routing {
        route("api/auth") {
            authenticateUserSession {
                userRoutes()
            }
            authRoutes()
        }
    }
}