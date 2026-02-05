package com.bieniucieniu.features.shared.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val reason: List<String>? = null,
)