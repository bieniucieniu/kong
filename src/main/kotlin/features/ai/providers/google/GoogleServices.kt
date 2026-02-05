package com.bieniucieniu.features.ai.providers.google

import ai.koog.prompt.executor.clients.google.GoogleModels
import com.bieniucieniu.features.ai.providers.ollama.ListModelsResponse

class GoogleService() {
    suspend fun getAvailableModels(): ListModelsResponse {
        GoogleModels
        return ListModelsResponse(listOf())
    }
}
