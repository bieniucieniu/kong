package com.bieniucieniu.features.ai.models

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ChatMessageAuthor {
    @SerialName("user")
    User,

    @SerialName("agent")
    Agent,

    @SerialName("tool")
    Tool;

    companion object {
        fun fromString(str: String): ChatMessageAuthor? {
            return when (str.trim().lowercase()) {
                "user" -> User
                "agent" -> Agent
                "tool" -> Tool
                else -> null
            }
        }
    }
}

@Serializable
data class ChatSession(
    val id: String,
    val name: String? = null,
    val systemPrompt: String? = null,
    val messages: List<ChatMessage> = emptyList()
)

@Serializable
data class ChatMessage(
    val role: ChatMessageAuthor,
    val content: String,
    val createAt: Long = System.currentTimeMillis()
)

@Serializable
data class ChatPrompt(val message: String, val model: String? = null, val provider: String? = null)

@Serializable
data class ChatPromptsList(val messages: List<ChatMessage>, val model: String? = null, val provider: String? = null)

@Serializable
data class SerializableLLMProvider(val id: String, val display: String) {
    fun toLLMProvider(): LLMProvider? = findProviderById(id)
}

@Serializable
data class SerializableLLModel(
    val provider: SerializableLLMProvider,
    val id: String,
    val capabilities: List<LLMCapability>,
    val contextLength: Long,
    val maxOutputTokens: Long? = null,
) {
    fun supports(capability: LLMCapability): Boolean = capabilities.any { it.id == capability.id }
    fun toLLModel(): LLModel? = LLModel(
        provider = provider.toLLMProvider() ?: return null,
        id = id,
        capabilities = capabilities,
        contextLength = contextLength,
        maxOutputTokens = maxOutputTokens,
    )
}

fun LLModel.toSerializableLLModel() = SerializableLLModel(
    provider = provider.toSerializableLLMProvider(),
    id = id,
    capabilities = capabilities,
    contextLength = contextLength,
    maxOutputTokens = maxOutputTokens,

    )

fun LLMProvider.toSerializableLLMProvider() = SerializableLLMProvider(id, display)

fun findProviderById(id: String): LLMProvider? =
    LLMProvider::class.nestedClasses
        .mapNotNull { it.objectInstance as? LLMProvider }
        .find { it.id.equals(id, ignoreCase = true) }

