package com.bieniucieniu.features.ai.services

import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.bieniucieniu.features.ai.providers.shared.AiProviderService

class AiService(val services: Map<String, AiProviderService>) {
    fun isActive(): Boolean = services.any { it.value.isActive() }

    suspend fun getAvailableLLModels(provider: String?): List<LLModel> =
        services[provider ?: getDefaultProvider()?.id]?.getAvailableLLModels() ?: emptyList()

    suspend fun getDefaultModel(provider: String?): LLModel? {
        return services[provider ?: getDefaultProvider()?.id]?.getDefaultModel()
    }

    fun getProviders(): List<LLMProvider> {
        return services.values.map { it.provider }
    }

    fun <T> getProviders(transform: (LLMProvider) -> T): List<T> {
        return services.values.map { transform(it.provider) }
    }

    fun getDefaultProvider(): LLMProvider? {
        return services.values.firstOrNull()?.provider
    }
}