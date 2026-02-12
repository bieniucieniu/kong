package com.bieniucieniu.features.auth.services

import com.bieniucieniu.features.auth.models.GoogleUser
import com.bieniucieniu.features.auth.models.UserSession
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*


class GoogleService(val client: HttpClient) {


    suspend fun getUser(accessToken: String): GoogleUser =
        client.get("https://www.googleapis.com/oauth2/v3/userinfo") {
            header("Authorization", "Bearer $accessToken")
        }.body()


    suspend fun getUser(session: UserSession): GoogleUser = getUser(session.accessToken)

    suspend fun revokeUser(session: UserSession): HttpResponse {
        // Determine which token to revoke.
        // It's best to revoke the refresh token if you have it, as it kills the whole session.
        val tokenToRevoke = session.refreshToken ?: session.accessToken

        return client.post("https://oauth2.googleapis.com/revoke") {
            // Google expects application/x-www-form-urlencoded
            contentType(ContentType.Application.FormUrlEncoded)

            setBody(FormDataContent(Parameters.build {
                append("token", tokenToRevoke)
            }))
        }
    }
}
