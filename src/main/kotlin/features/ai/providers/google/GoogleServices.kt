package com.bieniucieniu.features.ai.providers.google

import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.bieniucieniu.features.ai.providers.shared.AiProviderService
import io.ktor.server.application.*
import kotlin.reflect.full.memberProperties

class GoogleService(val application: Application) : AiProviderService {
    override val provider: LLMProvider = LLMProvider.Google
    var modelsCache: List<LLModel>? = null
    override suspend fun getAvailableLLModels(): List<LLModel> =
        modelsCache ?: GoogleModels::class.memberProperties
            .mapNotNull {
                try {
                    it.get(GoogleModels) as? LLModel
                } catch (_: Throwable) {
                    null
                }
            }
            .also {
                modelsCache = it
            }


    override fun isActive(): Boolean = application.environment.config.propertyOrNull("ai.google.apikey") != null

    override suspend fun getDefaultModel(): LLModel {
        return GoogleModels.Gemini2_0FlashLite
    }

}
