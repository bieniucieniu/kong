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
import io.ktor.util.logging.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class OllamaService(val httpClient: HttpClient, val application: Application) : AiProviderService {
    val predefined get(): Map<String, LLModel> = predefineModels
    private val predefineModels by lazy {
        val m = mutableMapOf<String, LLModel>()
        extractModels(
            OllamaModels.Meta,
            OllamaModels.Alibaba,
            OllamaModels.Groq,
            OllamaModels.Granite,
        ).associateByTo(m) { it.id }
        m
    }
    override val provider: LLMProvider = LLMProvider.Ollama
    private val ollamaBaseUrl =
        application.environment.config
            .propertyOrNull("ai.ollama.baseUrl")?.getString()
            ?.takeIf { it.isNotBlank() && (it.startsWith("http") || it.startsWith("https")) }
            ?.let { Url(it) }


    override fun isActive() = ollamaBaseUrl != null
    override suspend fun getAvailableLLModels(): List<LLModel> = getAvailableModels().models.mapNotNull {
        predefineModels[it.name]
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
            contentType(ContentType.Application.Json)
            setBody(OllamaModelArgs(model))
        }.body()
    } ?: OllamaStatusResponse("inactive")


    suspend fun ensureInstalledModels(tags: List<String>, logger: Logger = application.log) =
        getAvailableModels()
            .models.let { model ->
                tags.filter { tag -> model.find { it.name == tag } == null }
            }
            .let { tags ->
                coroutineScope {
                    tags.forEach { tag ->
                        launch {
                            logger.info("Pulling model: $tag")
                            val res = pullModel(tag)
                            logger.info("Pulled model: $tag with status ${res.status}")
                        }

                    }
                }
            }


    suspend fun getRunningModels(): ListModelsResponse = ollamaBaseUrl?.let { baseUrl ->
        httpClient.get(url {
            takeFrom(baseUrl)
            path("api", "ps")
        }).body()
    } ?: ListModelsResponse(emptyList())

    override suspend fun getDefaultModel(): LLModel = getAvailableLLModels().let { models ->
        models.find { it.id == DEFAULT_MODEL.id } ?: models.firstOrNull() ?: DEFAULT_MODEL
    }

    companion object {
        val GEMMA_3_4B = LLModel(
            provider = LLMProvider.Ollama,
            id = "gemma3:4b",
            capabilities = listOf(
                LLMCapability.Temperature,
                LLMCapability.Schema.JSON.Basic,
                LLMCapability.Tools
            ),
            contextLength = 40_960,
        )

        val DEFAULT_MODEL = GEMMA_3_4B

        fun createLLModel(
            id: String,
            provider: LLMProvider = LLMProvider.Ollama,
            capabilities: List<LLMCapability> = listOf(
                LLMCapability.Temperature,
                LLMCapability.Schema.JSON.Basic,
                LLMCapability.Tools
            ),
            contextLength: Long = 131_072,
        ) = LLModel(
            id = id,
            provider = provider,
            capabilities = capabilities,
            contextLength = contextLength
        )
    }
}


