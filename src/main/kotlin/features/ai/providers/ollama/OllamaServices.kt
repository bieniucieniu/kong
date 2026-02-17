package com.bieniucieniu.features.ai.providers.ollama

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.llm.OllamaModels
import com.bieniucieniu.features.ai.providers.shared.AiProviderService
import com.bieniucieniu.features.shared.models.extractModels
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class OllamaService(val httpClient: HttpClient, val application: Application) : AiProviderService {
    val predefineModels by lazy {
        extractModels(
            OllamaModels.Meta,
            OllamaModels.Alibaba,
            OllamaModels.Groq,
            OllamaModels.Granite
        ).associateBy { it.id }
    }
    override val provider: LLMProvider = LLMProvider.Ollama
    private val ollamaBaseUrl =
        application.environment.config
            .propertyOrNull("ai.ollama.baseUrl")?.getString()
            ?.takeIf { it.isNotBlank() && (it.startsWith("http") || it.startsWith("https")) }
            ?.let { Url(it) }


    override fun isActive() = ollamaBaseUrl != null
    override suspend fun getAvailableLLModels(): List<LLModel> {

        return getAvailableModels().models.map {
            predefineModels[it.name] ?: LLModel(
                id = it.name,
                provider = LLMProvider.Ollama,
                capabilities = listOf(
                    LLMCapability.Temperature,
                    LLMCapability.Schema.JSON.Basic,
                    LLMCapability.Tools,
                ),
                contextLength = 131_072,
            )
        }
    }

    suspend fun getAvailableModels(): ListModelsResponse = ollamaBaseUrl?.let { baseUrl ->
        httpClient.get(url {
            takeFrom(baseUrl)
            path("api", "tags")
        }).body<ListModelsResponse>()
    } ?: ListModelsResponse(emptyList())

    suspend fun pullModel(model: String): OllamaStatusResponse = ollamaBaseUrl?.let { baseUrl ->
        httpClient.post(url {
            takeFrom(baseUrl)
            path("api", "pull")

        }) {
            setBody(OllamaModelArgs(model))
        }.body()
    } ?: OllamaStatusResponse("inactive")


    suspend fun ensureInstalledModels(tags: List<String>) =
        getAvailableModels()
            .models.let { model ->
                tags.filter { tag -> model.find { it.name == tag } == null }
            }
            .let {
                coroutineScope {
                    it.forEach { model -> launch { pullModel(model) } }
                }
            }


    suspend fun getRunningModels(): ListModelsResponse = ollamaBaseUrl?.let { baseUrl ->
        httpClient.get(url {
            takeFrom(baseUrl)
            path("api", "ps")
        }).body()
    } ?: ListModelsResponse(emptyList())

    override suspend fun getDefaultModel(): LLModel = getAvailableLLModels().let { models ->
        models.find { it.id == "gemma3:4b" } ?: models.firstOrNull() ?: GEMMA_3_4B
    }
}


val GEMMA_3_4B = LLModel(
    provider = LLMProvider.Ollama,
    id = "gemma3:4b",
    capabilities = listOf(
        LLMCapability.Temperature,
        LLMCapability.Schema.JSON.Basic,
        LLMCapability.Tools
    ),
    contextLength = 131_072,
)