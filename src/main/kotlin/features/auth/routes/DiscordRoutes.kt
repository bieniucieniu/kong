package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.utils.io.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

@OptIn(InternalAPI::class)
fun Route.authDiscordRoutes(
    onAuth: suspend (OAuthAccessTokenResponse.OAuth2) -> UserSession = { principal ->
        UserSession(
            accessToken = principal.accessToken,
            refreshToken = principal.refreshToken,
            expiredIn = principal.expiresIn,
            provider = OAuth2Provider.Discord,
            userId = Uuid.NIL
        )
    }
) {
    val client: HttpClient by inject()
    val authPlugin = application.pluginOrNull(Authentication) ?: return
    val providers = authPlugin.configuration().allProviders()
    if (providers.containsKey("auth-oauth-discord"))
        authenticate("auth-oauth-discord") {
            route("discord") {
                get("login") {
                    call.respondRedirect("callback")
                }
                get("callback") {
                    val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                    if (principal != null) {
                        call.sessions.set(onAuth(principal))
                        call.respondRedirect("/")
                    } else call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid/no credentials"))

                }
                get("revoke") {
                    val session = call.sessions.get<UserSession>()
                    if (session?.accessToken != null) {
                        val res = client.get("https://discord.com/api/oauth2/token/revoke") {
                            parameter("token", session.accessToken)
                        }
                        when (res.status) {
                            HttpStatusCode.OK -> call.sessions.clear<UserSession>()
                        }

                    }
                    call.respondRedirect("/")
                }
            }
        }
}