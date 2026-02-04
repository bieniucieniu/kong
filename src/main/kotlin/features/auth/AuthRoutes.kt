package com.bieniucieniu.features.auth

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    authenticate("auth-oauth-google") {
        val client: HttpClient by inject()
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
            get("revoke") {
                val session = call.sessions.get<UserSession>()
                if (session?.accessToken != null) {
                    val res = client.get("https://oauth2.googleapis.com/revoke") {
                        parameter("token", session.accessToken)
                    }

                    call.sessions.clear<UserSession>()
                }
                call.respondRedirect("/")
            }
        }
    }
}