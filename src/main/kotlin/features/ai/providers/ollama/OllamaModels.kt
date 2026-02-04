package com.bieniucieniu.features.ai.providers.ollama

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListModelsResponse(val models: List<OllamaModel>)

@Serializable
data class OllamaModel(
    val name: String,
    @SerialName("modified_at")
    val modifiedAt: String,
    val size: Int,
    val digest: String,
    val details: OllamaModelDetails
)

@Serializable
data class OllamaModelDetails(
    val format: String,
    val family: String,
    val families: List<String>,
    @SerialName("parameter_size")
    val parameterSize: String,
    @SerialName("quantization_level")
    val quantizationLevel: String,
)