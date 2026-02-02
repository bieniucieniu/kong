package com.bieniucieniu.features.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ChatMessageAuthor {
    @SerialName("user")
    User,

    @SerialName("agent")
    Agent,

    @SerialName("tool")
    Tool,
}

@Serializable
data class ChatMessage(val prompt: String, val author: ChatMessageAuthor)

@Serializable
data class Chat(val messages: List<ChatMessage>)
