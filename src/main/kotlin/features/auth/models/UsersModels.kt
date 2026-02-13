package com.bieniucieniu.features.auth.models

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class User(
    val id: Uuid,
    val username: String,
    val googleId: String?,
    val discordId: ULong?,
) {
}