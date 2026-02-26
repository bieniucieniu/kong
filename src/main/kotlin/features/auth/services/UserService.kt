package com.bieniucieniu.features.auth.services

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.repositories.UserDao
import com.bieniucieniu.features.auth.repositories.UsersTable
import io.ktor.client.*
import io.ktor.client.statement.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import kotlin.uuid.Uuid

class UserService(val discordService: DiscordService, val googleService: GoogleService, val client: HttpClient) {
    suspend fun getUserById(id: Uuid) = suspendTransaction { UserDao.findById(id)?.toUser() }
    suspend fun getUserBySession(userSession: UserSession): User? =
        suspendTransaction {
            getUserById(userSession.userId)?.copy(
                avatar = getAvatarUrl(userSession)
            ) ?: when (userSession.provider) {
                OAuth2Provider.Discord -> {
                    val du = discordService.getUser(userSession)
                    val avatar = discordService.getAvatarUrl(du)
                    suspendTransaction {
                        UserDao.find { UsersTable.discordId eq du.id }.firstOrNull()
                            ?.toUser(avatar = avatar)
                    }
                }

                OAuth2Provider.Google -> {
                    val gu = googleService.getUser(userSession)
                    suspendTransaction {
                        UserDao.find { UsersTable.googleId eq gu.sub }.firstOrNull()?.toUser()
                    }
                }

                else -> User(
                    id = userSession.userId,
                    username = userSession.username ?: "<unknown>",
                    googleId = null,
                    discordId = null
                )
            }
        }

    suspend fun getAvatarUrl(userSession: UserSession) = when (userSession.provider) {
        OAuth2Provider.Discord -> discordService.getAvatarUrl(userSession)
        else -> null
    }

    suspend fun createUserBySession(userSession: UserSession): User =
        suspendTransaction {
            when (userSession.provider) {
                OAuth2Provider.Discord -> {
                    val du = discordService.getUser(userSession)
                    UserDao.new {
                        username = du.username
                        discordId = du.id
                    }.toUser()
                }

                OAuth2Provider.Google -> {
                    val gu = googleService.getUser(userSession)
                    UserDao.new {
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

    suspend fun callRevokeUser(userSession: UserSession): HttpResponse? =
        when (userSession.provider) {
            OAuth2Provider.Discord -> discordService.revokeUser(userSession)
            OAuth2Provider.Google -> googleService.revokeUser(userSession)
            else -> null
        }
}