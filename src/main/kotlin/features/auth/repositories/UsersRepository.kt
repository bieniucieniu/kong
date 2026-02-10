package com.bieniucieniu.features.auth.repositories

import com.bieniucieniu.features.auth.models.User
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UIntIdTable
import org.jetbrains.exposed.v1.dao.UIntEntity
import org.jetbrains.exposed.v1.dao.UIntEntityClass


const val MAX_VARCHAR_LENGTH = 255


object UsersTable : UIntIdTable("users") {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val googleId = varchar("google_id", MAX_VARCHAR_LENGTH).nullable()
    val discordId = varchar("discord_id", MAX_VARCHAR_LENGTH).nullable()
}

class UserEntity(id: EntityID<UInt>) : UIntEntity(id) {
    companion object : UIntEntityClass<UserEntity>(UsersTable) {
        fun fromUser(user: User) = new {
            name = user.name
            googleId = user.googleId
            discordId = user.discordId
        }
    }

    fun toUser() = User(id.value, name, googleId, discordId)

    var name by UsersTable.name
    var googleId by UsersTable.googleId
    var discordId by UsersTable.discordId
    override fun toString(): String {
        return "User(id=$id, name='$name', google_id=$googleId, discord_id=$discordId)"
    }
}
