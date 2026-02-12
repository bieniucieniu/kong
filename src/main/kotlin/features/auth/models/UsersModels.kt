package com.bieniucieniu.features.auth.models

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: ULong,
    val username: String,
    val googleId: String?,
    val discordId: ULong?,
) {
}