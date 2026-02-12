package com.bieniucieniu.auth

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.auth.oauth2.configureDiscordOauth2
import com.bieniucieniu.auth.oauth2.installGoogleOauth2
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject


fun Application.installAuthPlugins() {
    val httpClient by inject<HttpClient>()

    val frontendUrl by lazy {
        environment.config.propertyOrNull("frontend.url")?.getString() ?: "http://localhost:3000"
    }

    install(Sessions) {
        cookie<UserSession>("user-session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }



    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                when (session.provider) {
                    OAuth2Provider.Google -> session
                    OAuth2Provider.Discord -> session
                    null -> null
                }
            }
            challenge {
                // Optional: what to do if session is missing
                call.respond(HttpStatusCode.Unauthorized, "No session found")
            }
        }
        installGoogleOauth2(
            clientId =
                this@installAuthPlugins.environment.config.propertyOrNull("oauth2.google.clientId")?.getString(),
            clientSecret =
                this@installAuthPlugins.environment.config.propertyOrNull("oauth2.google.clientSecret")?.getString(),
            frontendUrl = frontendUrl,
            httpClient = httpClient
        )
        configureDiscordOauth2(
            clientId =
                this@installAuthPlugins.environment.config.propertyOrNull("oauth2.discord.clientId")?.getString(),
            clientSecret =
                this@installAuthPlugins.environment.config.propertyOrNull("oauth2.discord.clientSecret")?.getString(),
            frontendUrl = frontendUrl,
            httpClient = httpClient
        )
    }

}