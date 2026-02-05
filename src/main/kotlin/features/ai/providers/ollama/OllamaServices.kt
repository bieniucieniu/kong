package com.bieniucieniu.features.ai.providers.ollama

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider.Ollama
import ai.koog.prompt.llm.LLModel
import com.bieniucieniu.features.ai.SerializableLLModel
import com.bieniucieniu.features.ai.toSerializableLLMProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class OllamaService(val httpClient: HttpClient, val application: Application) : KoinComponent {
    private val ollamaBaseUrl =
        application.environment.config
            .propertyOrNull("koog.ollama.baseUrl")?.getString()
            ?.takeIf { it.isNotBlank() }
            ?.let { Url(it) }

    private var modelsCache: ListModelsResponse? = null

    suspend fun getAvailableLLModels(): List<SerializableLLModel> {
        return getAvailableModels().models.map {
            SerializableLLModel(
                id = it.name,
                provider = Ollama.toSerializableLLMProvider(),
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
        modelsCache ?: httpClient.get(url {
            takeFrom(baseUrl)
            path("api", "tags")
        }).body<ListModelsResponse>().also {
            modelsCache = it
        }
    } ?: ListModelsResponse(emptyList())

    suspend fun pullModel(model: String): OllamaStatusResponse = ollamaBaseUrl?.let { baseUrl ->
        httpClient.post(url {
            takeFrom(baseUrl)
            path("api", "tags")

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
}


fun getDefaultModel() = GEMMA_3_4B


val GEMMA_3_4B = LLModel(
    provider = Ollama,
    id = "gemma3:4b",
    capabilities = listOf(
        LLMCapability.Temperature,
        LLMCapability.Schema.JSON.Basic,
        LLMCapability.Tools
    ),
    contextLength = 131_072,
)


