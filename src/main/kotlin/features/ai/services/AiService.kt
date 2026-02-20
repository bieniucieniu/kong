package com.bieniucieniu.features.ai.services

import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.bieniucieniu.features.ai.providers.shared.AiProviderService

class AiService(val services: Map<String, AiProviderService>) {
    constructor(
        services: List<AiProviderService>
    ) : this(
        services
            .filter { it.isActive() }
            .associateBy { it.provider.id }
    )

    fun isActive(): Boolean = services.any { it.value.isActive() }
    suspend fun getAvailableLLModels(provider: String? = null): List<LLModel> =
        getService(provider)?.getAvailableLLModels() ?: emptyList()

    suspend fun getModel(model: String? = null, provider: String? = null) =
        getAvailableLLModels(provider).find { it.id == model } ?: getDefaultModel(provider)

    suspend fun getDefaultModel(provider: String? = null): LLModel? = getService(provider)?.getDefaultModel()
    fun getService(provider: String? = null): AiProviderService? = (services[provider] ?: getDefaultService())
    fun getDefaultService(): AiProviderService? = services.values.firstOrNull()

    inline fun <T> getProviders(fn: (LLMProvider) -> T): List<T> = services.values.map { fn(it.provider) }
    fun getProviders() = getProviders { it }
}


