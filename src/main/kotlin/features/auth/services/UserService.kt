package com.bieniucieniu.features.auth.services

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.repositories.UserEntity
import com.bieniucieniu.features.auth.repositories.UsersTable
import io.ktor.client.statement.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import kotlin.uuid.Uuid

class UserService(val discordService: DiscordService, val googleService: GoogleService) {
    suspend fun getUserById(id: Uuid) = suspendTransaction { UserEntity.findById(id)?.toUser() }
    suspend fun getUserBySession(userSession: UserSession): User? =
        suspendTransaction {
            userSession.userId?.let { getUserById(userSession.userId) }
                ?: when (userSession.provider) {
                    OAuth2Provider.Discord -> {
                        val du = discordService.getUser(userSession)
                        suspendTransaction {
                            UserEntity.find { UsersTable.discordId eq du.id }.firstOrNull()?.toUser()
                        }
                    }

                    OAuth2Provider.Google -> {
                        val gu = googleService.getUser(userSession)
                        suspendTransaction {
                            UserEntity.find { UsersTable.googleId eq gu.sub }.firstOrNull()?.toUser()
                        }
                    }

                    else -> User(
                        id = userSession.userId ?: Uuid.NIL,
                        username = userSession.username ?: "<unknown>",
                        googleId = null,
                        discordId = null
                    )
                }
        }


    suspend fun createUserBySession(userSession: UserSession): User =
        suspendTransaction {
            when (userSession.provider) {
                OAuth2Provider.Discord -> {
                    val du = discordService.getUser(userSession)
                    UserEntity.new {
                        username = du.username
                        discordId = du.id
                    }.toUser()
                }

                OAuth2Provider.Google -> {
                    val gu = googleService.getUser(userSession)
                    UserEntity.new {
                        username = gu.name
                        googleId = gu.sub
                    }.toUser()
                }

                else -> User(
                    id = userSession.userId ?: Uuid.NIL,
                    username = userSession.username ?: "<unknown>",
                    googleId = null,
                    discordId = null
                )
            }
        }

    suspend fun ensureUserBySession(userSession: UserSession): User =
        suspendTransaction {
            getUserBySession(userSession) ?: createUserBySession(userSession)
        }

    suspend fun revokeUser(userSession: UserSession): HttpResponse? =
        when (userSession.provider) {
            OAuth2Provider.Discord -> discordService.revokeUser(userSession)
            OAuth2Provider.Google -> googleService.revokeUser(userSession)
            else -> null
        }
}