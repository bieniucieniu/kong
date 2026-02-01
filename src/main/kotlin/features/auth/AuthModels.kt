package com.bieniucieniu.features.auth

import kotlinx.serialization.Serializable

enum class OAuth2Provider {
    Google,
    Discord,
}

@Serializable
data class UserSession(val accessToken: String, val provider: OAuth2Provider? = null)