package com.bieniucieniu.features.auth

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.routes.authRoutes
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject


fun Application.configureAuth() {
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
        run {
            val clientId = this@configureAuth.environment.config.propertyOrNull("oauth2.google.clientId")?.getString()
            val clientSecret =
                this@configureAuth.environment.config.propertyOrNull("oauth2.google.clientSecret")?.getString()
            if (clientId != null && clientSecret != null)
                oauth("auth-oauth-google") {
                    urlProvider = {
                        "${frontendUrl}/api/auth/google/callback"
                    }
                    providerLookup = {
                        OAuthServerSettings.OAuth2ServerSettings(
                            name = "google",
                            authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                            accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                            requestMethod = HttpMethod.Post,
                            clientId = clientId,
                            clientSecret = clientSecret,
                            defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                        )
                    }
                    client = httpClient
                }
        }
        run {
            val clientId =
                this@configureAuth.environment.config.propertyOrNull("oauth2.discord.clientId")?.getString()
            val clientSecret =
                this@configureAuth.environment.config.propertyOrNull("oauth2.discord.clientSecret")?.getString()
            if (clientId != null && clientSecret != null)
                oauth("auth-oauth-discord") {
                    urlProvider = { "${frontendUrl}/api/auth/discord/callback" }
                    providerLookup = {
                        OAuthServerSettings.OAuth2ServerSettings(
                            name = "discord",
                            authorizeUrl = "https://discord.com/oauth2/authorize",
                            accessTokenUrl = "https://discord.com/api/oauth2/token",
                            requestMethod = HttpMethod.Post,
                            clientId = clientId,
                            clientSecret = clientSecret,
                            defaultScopes = listOf("identify", "email"),
                        )
                    }
                    client = httpClient
                }
        }
    }

    routing {
        route("api/auth") {
            authRoutes()
        }
    }
}