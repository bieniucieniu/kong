package com.bieniucieniu.auth.oauth2

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*

/**
 * if [clientId] or [clientSecret] is null auth won't be installed
 */
fun AuthenticationConfig.installGoogleOauth2(
    key: String,
    clientId: String?,
    clientSecret: String?,
    frontendUrl: String?,
    httpClient: HttpClient,
) {
    if (clientId != null && clientSecret != null)
        oauth(key) {
            urlProvider = { "${frontendUrl}/api/auth/google/callback" }
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
