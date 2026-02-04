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
data class OllamaRunningModel(
    val name: String,
    @SerialName("modified_at")
    val size: Int,
    val digest: String,
    val details: OllamaModelDetails,
    @SerialName("expires_at")
    val expiresAt: String,
    @SerialName("size_vram")
    val sizeVRam: Long,
    @SerialName("context_length")
    val contextLength: Int,
) {
    fun toOllamaModel(): OllamaModel = OllamaModel(
        name = name,
        modifiedAt = "",
        size = size,
        digest = digest,
        details = details
    )
}

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

@Serializable
data class OllamaModelArgs(
    val model: String
)

@Serializable
data class OllamaStatusResponse(val status: String)