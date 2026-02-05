package com.bieniucieniu.features.auth

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Application.configureAuth() {
    val httpClient by inject<HttpClient>()
    val clientId = environment.config.property("oauth2.google.clientId").getString()
    val clientSecret = environment.config.property("oauth2.google.clientSecret").getString()

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }

    install(Authentication) {
        oauth("auth-oauth-google") {
            urlProvider = {
                "${request.local.scheme}://${request.local.serverHost}:${request.local.serverPort}/google/callback"
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

    routing {
        route("api") {
            authRoutes()
        }
    }
}