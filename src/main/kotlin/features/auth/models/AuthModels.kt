package com.bieniucieniu.features.auth.models

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

enum class OAuth2Provider {
    Google,
    Discord,
}

@Serializable
data class UserSession(
    val accessToken: String,
    val refreshToken: String?,
    val username: String? = null,
    val userId: Uuid,
    val expiredIn: Long? = null,
    val provider: OAuth2Provider? = null,
    val params: Map<String, List<String>?>? = null
)