package com.bieniucieniu.features.ai.providers.shared

import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel


interface AiProviderService {
    val provider: LLMProvider

    fun isActive(): Boolean

    suspend fun getAvailableLLModels(): List<LLModel>

    suspend fun getDefaultModel(): LLModel
}
