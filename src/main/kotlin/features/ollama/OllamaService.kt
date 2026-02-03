package com.bieniucieniu.features.ollama

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.llm.OllamaModels
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlin.reflect.full.memberProperties

class OllamaService(val httpClient: HttpClient) {
    private fun Application.getOllamaBaseUrl() = environment.config.property("koog.ollama.baseUrl").getString()

    suspend fun Application.getAvailableModels(): ListModelsResponse {
        val res = httpClient.get(url {
            takeFrom(Url(getOllamaBaseUrl()))
            path("api", "tags")
        })

        return res.body()
    }

    suspend fun Application.getRunningModels(): ListModelsResponse {
        val res = httpClient.get(url {
            takeFrom(Url(getOllamaBaseUrl()))
            path("api", "ps")
        })

        return res.body()
    }

}

fun getOssModels(): Map<String, LLModel> = _ossModels

fun getDefaultModel() = GoogleOssModels.GEMMA_3

private val _ossModels by lazy {
    val ollamaMetaModels = MetaOssModels::class.memberProperties
        .mapNotNull { it.get(MetaOssModels) as? LLModel }

    val googleOssModels = GoogleOssModels::class.memberProperties
        .mapNotNull { it.get(GoogleOssModels) as? LLModel }

    (ollamaMetaModels + googleOssModels)
        .associateBy { it.id }
}


object GoogleOssModels {
    val GEMMA_3: LLModel = LLModel(
        provider = LLMProvider.Ollama,
        id = "gemma3:4b",
        capabilities = listOf(
            LLMCapability.Temperature,
            LLMCapability.Schema.JSON.Basic,
            LLMCapability.Tools
        ),
        contextLength = 131_072,
    )
}

object MetaOssModels {
    val LLAMA_3_2 = OllamaModels.Meta.LLAMA_3_2_3B
}
