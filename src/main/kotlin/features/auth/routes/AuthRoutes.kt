package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.services.UserService
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.koin.ktor.ext.inject

@OptIn(InternalAPI::class)
fun Route.authRoutes() {
    val s: UserService by inject()
    userRoutes()
    authGoogleRoutes()
    authDiscordRoutes {
        val us = UserSession(
            accessToken = it.accessToken,
            refreshToken = it.refreshToken,
            expiredIn = it.expiresIn,
            provider = OAuth2Provider.Discord,
        )
        val u = s.ensureUser(us)
        us.copy(
            userId = u.id,
            username = u.username,
        )
    }
}