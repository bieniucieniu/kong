package com.bieniucieniu.features.ai.routes

import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.server.routing.*
import kotlin.time.Duration.Companion.minutes


fun Route.aiRoutes() {
    chatRoutes()
    cacheOutput(30.minutes) {
        providerRoutes()
        modelRoutes()
    }
}