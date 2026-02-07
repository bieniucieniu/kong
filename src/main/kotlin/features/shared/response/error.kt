package com.bieniucieniu.features.shared.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val reason: List<String>? = null,
)