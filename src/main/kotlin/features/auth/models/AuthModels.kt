package com.bieniucieniu.features.auth.models

import io.ktor.http.*
import kotlinx.serialization.Serializable

enum class OAuth2Provider {
    Google,
    Discord,
}

@Serializable
data class UserSession(
    val accessToken: String,
    val provider: OAuth2Provider? = null,
    val params: Parameters? = null
)