package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.services.UserService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.authRoutes() {
    val s: UserService by inject()
    userRoutes()
    authGoogleRoutes {
        val us = UserSession(
            accessToken = it.accessToken,
            refreshToken = it.refreshToken,
            expiredIn = it.expiresIn,
            provider = OAuth2Provider.Google,
            userId = Uuid.NIL,
        )
        val u = s.ensureUserBySession(us)
        us.copy(
            userId = u.id,
            username = u.username,
        )
    }
    authDiscordRoutes {
        val us = UserSession(
            accessToken = it.accessToken,
            refreshToken = it.refreshToken,
            expiredIn = it.expiresIn,
            provider = OAuth2Provider.Discord,
            userId = Uuid.NIL
        )
        val u = s.ensureUserBySession(us)
        us.copy(
            userId = u.id,
            username = u.username,
        )
    }
}