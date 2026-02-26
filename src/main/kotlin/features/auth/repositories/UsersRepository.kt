package com.bieniucieniu.features.auth.repositories

import com.bieniucieniu.features.auth.models.User
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid


const val MAX_VARCHAR_LENGTH = 255


object UsersTable : UuidTable("users") {
    val username = varchar("username", MAX_VARCHAR_LENGTH)
    val googleId = varchar("google_id", MAX_VARCHAR_LENGTH).nullable().uniqueIndex()
    val discordId = ulong("discord_id").nullable().uniqueIndex()
}

class UserDao(id: EntityID<Uuid>) : UuidEntity(id) {

    companion object : UuidEntityClass<UserDao>(UsersTable)

    fun toUser(
        id: Uuid? = null,
        username: String? = null,
        googleId: String? = null,
        discordId: ULong? = null,
        avatar: String? = null
    ) = User(
        id = id ?: this.id.value,
        username = username ?: this.username,
        googleId = googleId ?: this.googleId,
        discordId = discordId ?: this.discordId,
        avatar = avatar
    )

    var username by UsersTable.username
    var googleId by UsersTable.googleId
    var discordId by UsersTable.discordId
    override fun toString(): String {
        return "User(id=$id, name='$username', google_id=$googleId, discord_id=$discordId)"
    }
}
