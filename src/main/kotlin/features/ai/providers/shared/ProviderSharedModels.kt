package com.bieniucieniu.features.ai.providers.shared

import ai.koog.prompt.llm.LLModel


interface AiProviderService {
    val name: String
        get() = this::class.simpleName?.removeSuffix("Service") ?: "Unknown"

    fun isActive(): Boolean

    suspend fun getAvailableLLModels(): List<LLModel>

    suspend fun getDefaultModel(): LLModel
}
