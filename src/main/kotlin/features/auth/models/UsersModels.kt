package com.bieniucieniu.features.auth.models

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: UInt,
    val name: String,
    val googleId: String?,
    val discordId: String?,
) {
}