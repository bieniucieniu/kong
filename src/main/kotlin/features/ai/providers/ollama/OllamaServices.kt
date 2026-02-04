package com.bieniucieniu.features.ai.providers.ollama

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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.reflect.full.memberProperties

class OllamaService(val httpClient: HttpClient) : KoinComponent {
    private fun Application.getOllamaBaseUrl() = environment.config.property("koog.ollama.baseUrl").getString()

    suspend fun Application.getAvailableModels(): ListModelsResponse =
        httpClient.get(url {
            takeFrom(Url(getOllamaBaseUrl()))
            path("api", "tags")
        }).body()

    suspend fun Application.pullModel(model: String): OllamaStatusResponse =
        httpClient.post(url {
            takeFrom(Url(getOllamaBaseUrl()))
            path("api", "tags")

        }) {
            setBody(OllamaModelArgs(model))
        }.body()


    suspend fun Application.ensureInstalledModels(vararg tags: String) = ensureInstalledModels(tags.toList())
    suspend fun Application.ensureInstalledModels(tags: List<String>) =
        getAvailableModels()
            .models.let { model ->
                tags.filter { tag -> model.find { it.name == tag } == null }
            }
            .let {
                coroutineScope {
                    it.forEach { model -> launch { pullModel(model) } }
                }
            }


    suspend fun Application.getRunningModels(): ListModelsResponse =
        httpClient.get(url {
            takeFrom(Url(getOllamaBaseUrl()))
            path("api", "ps")
        }).body()
}

val ollamaModule = module {
    single {
        OllamaService(get(named("ollama-http-client")))
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
