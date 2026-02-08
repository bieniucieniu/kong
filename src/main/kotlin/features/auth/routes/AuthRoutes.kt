package com.bieniucieniu.features.auth.routes

import io.ktor.server.routing.*
import io.ktor.utils.io.*

@OptIn(InternalAPI::class)
fun Route.authRoutes() {
    authGoogleRoutes()
    authDiscordRoutes()
}