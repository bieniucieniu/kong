package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.auth.authenticateUserSession
import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.server.routing.*
import kotlin.time.Duration.Companion.minutes


fun Route.aiRoutes() {
    authenticateUserSession {
        chatRoutes()
    }
    cacheOutput(30.minutes) {
        providerRoutes()
        modelRoutes()
    }
}