package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.features.ai.services.AiService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.sessionRoutes() {
    val s: AiService by inject()
    route("sessions") {
        get {
        }
    }
}
