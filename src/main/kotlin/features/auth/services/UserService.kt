package com.bieniucieniu.features.auth.services

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.repositories.UserEntity
import com.bieniucieniu.features.auth.repositories.UsersTable
import io.ktor.client.statement.*
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class UserService(val discordService: DiscordService) {
    suspend fun getUser(userSession: UserSession): User? = when (userSession.provider) {
        OAuth2Provider.Discord -> {
            val du = discordService.getUser(userSession)
            transaction {
                UserEntity.find { UsersTable.discordId eq du.id }.firstOrNull()?.toUser()
            }
        }

        else -> User(
            id = userSession.userId ?: 0u,
            username = userSession.username ?: "<unknown>",
            googleId = null,
            discordId = null
        )
    }

    suspend fun createUser(userSession: UserSession): User =
        when (userSession.provider) {

            OAuth2Provider.Discord -> {
                val du = discordService.getUser(userSession)
                transaction {
                    UserEntity.new {
                        username = du.username
                        discordId = du.id
                    }.toUser()
                }
            }

            else -> User(
                id = userSession.userId ?: 0u,
                username = userSession.username ?: "<unknown>",
                googleId = null,
                discordId = null
            )
        }

    suspend fun ensureUser(userSession: UserSession): User = getUser(userSession) ?: createUser(userSession)

    suspend fun revokeUser(userSession: UserSession, config: ApplicationConfig): HttpResponse? =
        when (userSession.provider) {

            // OAuth2Provider.Discord ->
            else -> discordService.revokeUser(
                userSession,
                clientId = config.propertyOrNull("oauth2.discord.clientId")?.getString()!!,
                clientSecret = config.propertyOrNull("oauth2.discord.clientSecret")
                    ?.getString()!!
            )
        }
}