package com.bieniucieniu.features.auth.services

import com.bieniucieniu.features.auth.models.DiscordUser
import com.bieniucieniu.features.auth.models.UserSession
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*


class DiscordService(val client: HttpClient, application: Application) {
    val clientId = application.environment.config.propertyOrNull("oauth2.discord.clientId")?.getString()!!
    val clientSecret = application.environment.config.propertyOrNull("oauth2.discord.clientSecret")
        ?.getString()!!

    suspend fun getUser(accessToken: String): DiscordUser =
        client.get("https://discord.com/api/users/@me") {
            header("Authorization", "Bearer $accessToken")
        }.body()

    suspend fun getUser(session: UserSession): DiscordUser = getUser(session.accessToken)

    suspend fun revokeUser(session: UserSession): HttpResponse {
        if (session.refreshToken != null) {
            val res = client.post("https://discord.com/api/oauth2/token/revoke") {
                header("Authorization", "Bearer ${session.accessToken}")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("token", session.refreshToken) // The refresh token or access token
                    append("token_type_hint", "refresh_token")
                }))
            }
            if (res.status.value !in 200..299) return res
        }
        val res = client.post("https://discord.com/api/oauth2/token/revoke") {
            header("Authorization", "Bearer ${session.accessToken}")
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("token", session.accessToken) // The refresh token or access token
                append("token_type_hint", "access_token")
            }))
        }
        return res
    }
}