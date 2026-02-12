package com.bieniucieniu.features.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class GoogleUser(
    val sub: String,
    val name: String,
    val email: String? = null,
    val picture: String? = null
)