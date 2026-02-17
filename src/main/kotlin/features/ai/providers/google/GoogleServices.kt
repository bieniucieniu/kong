package com.bieniucieniu.features.ai.providers.google

import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.bieniucieniu.features.ai.providers.shared.AiProviderService
import com.bieniucieniu.features.shared.models.extractModels
import io.ktor.server.application.*

class GoogleService(val application: Application) : AiProviderService {
    override val provider: LLMProvider = LLMProvider.Google
    val lazyModels: List<LLModel> by lazy {
        extractModels(GoogleModels, GoogleModels.Embeddings)

    }

    override suspend fun getAvailableLLModels(): List<LLModel> = lazyModels


    override fun isActive(): Boolean = application.environment.config.propertyOrNull("ai.google.apikey") != null

    override suspend fun getDefaultModel(): LLModel {
        return GoogleModels.Gemini2_0FlashLite
    }

}

