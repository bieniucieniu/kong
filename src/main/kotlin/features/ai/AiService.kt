package com.bieniucieniu.features.ai

import ai.koog.prompt.llm.LLModel
import com.bieniucieniu.features.ai.providers.shared.AiProviderService

class AiService(val services: Map<String, AiProviderService>) {
    fun isActive(): Boolean = services.any { it.value.isActive() }

    suspend fun getAvailableLLModels(provider: String): List<LLModel> =
        services[provider]?.getAvailableLLModels() ?: emptyList()

    suspend fun getDefaultModel(provider: String): LLModel? {
        return services[provider]?.getDefaultModel()
    }

    fun getProvidersNames(): List<String> {
        return services.keys.toList()
    }

    fun getDefaultProvider(): String? {
        return services.keys.firstOrNull()
    }
}