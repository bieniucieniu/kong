package com.bieniucieniu.features.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.authRoutes() {
/*    authenticate("auth-oauth-discord") {
        route("/discord") {
            get("login") {
                call.respondRedirect("callback")
            }
            get("callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                call.sessions.set(
                    UserSession(
                        accessToken = principal?.accessToken.toString(),
                        provider = OAuth2Provider.Discord
                    )
                )
                call.respondRedirect("/")
            }
        }
    }*/
    authenticate("auth-oauth-google") {
        route("/google") {
            get("login") {
                call.respondRedirect("callback")
            }
            get("callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                call.sessions.set(
                    UserSession(
                        accessToken = principal?.accessToken.toString(),
                        provider = OAuth2Provider.Google
                    )
                )
                call.respondRedirect("/")
            }
        }
    }
}