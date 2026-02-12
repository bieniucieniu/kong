package com.bieniucieniu.auth.oauth2

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*

/**
 * if [clientId] or [clientSecret] is null auth won't be installed
 */
fun AuthenticationConfig.configureDiscordOauth2(
    clientId: String?,
    clientSecret: String?,
    frontendUrl: String?,
    httpClient: HttpClient,
) {
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