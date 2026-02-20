package com.bieniucieniu.features.ai.services

import ai.koog.prompt.dsl.PromptBuilder
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.params.LLMParams
import com.bieniucieniu.auth.getUserSession
import com.bieniucieniu.features.ai.models.ChatMessage
import com.bieniucieniu.features.ai.models.ChatMessageAuthor
import com.bieniucieniu.features.ai.models.ChatPromptsList
import com.bieniucieniu.features.ai.models.ChatSession
import com.bieniucieniu.features.ai.repositories.ChatMessageDao
import com.bieniucieniu.features.ai.repositories.ChatMessageTable
import com.bieniucieniu.features.ai.repositories.ChatSessionDao
import com.bieniucieniu.features.ai.repositories.ChatSessionTable
import com.bieniucieniu.features.auth.models.UserSession
import io.ktor.server.routing.*
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import kotlin.uuid.Uuid

class ChatService {
    fun createChatSession(name: String?, systemPrompt: String?, session: UserSession) =
        createChatSession(name, systemPrompt, session.userId)

    fun createChatSession(name: String?, systemPrompt: String?, userId: Uuid): ChatSession = ChatSessionDao.new {
        this.ownerId = userId
        this.name = name
        this.systemPrompt = systemPrompt
    }.toChatSession()


    fun getUserChatSession(
        id: Uuid,
        session: UserSession,
        includeMessages: Boolean = false,
        forUpdate: Boolean = true
    ) =
        getUserChatSession(
            id,
            session.userId,
            includeMessages = includeMessages,
            forUpdate = forUpdate
        )


    fun getUserChatSession(
        id: Uuid,
        userId: Uuid,
        includeMessages: Boolean = false,
        forUpdate: Boolean = true,
    ) = ChatSessionDao.find {
        (ChatSessionTable.id eq id) and (ChatSessionTable.ownerId eq userId)
    }.let { if (forUpdate) it.forUpdate() else it }.firstOrNull()?.toChatSession(includeMessages = includeMessages)

    fun getUserChatSessionMessages(
        id: Uuid,
        from: Long,
        count: Int,
        forUpdate: Boolean = true,
    ) = ChatMessageDao
        .find(ChatMessageTable.sessionId eq id)
        .orderBy(ChatMessageTable.createdAt to SortOrder.ASC)
        .offset(from).limit(count).let {
            if (forUpdate) it.forUpdate() else it
        }


    fun getUserChatSessionsList(
        call: RoutingContext
    ) = getUserChatSessionsList(call.getUserSession().userId)


    fun getUserChatSessionsList(
        userId: Uuid
    ) = ChatSessionDao.find(ChatSessionTable.ownerId eq userId).map { it.toChatSession() }


    fun saveMessage(
        chatSessionId: Uuid,
        role: ChatMessageAuthor,
        content: String
    ): ChatMessage {
        val o = ChatMessageDao.new {
            sessionId = chatSessionId
            this.role = role
            this.content = content
        }
        val s = ChatSessionDao.find { ChatSessionTable.id eq o.sessionId }
        return o.toChatMessage()
    }
}

const val BASE_SYSTEM_PROMPT = "You are a helpful assistant. Write short, simple and concise answers"

fun ChatSession.buildPrompt(
    id: String = "chat",
    params: LLMParams = LLMParams(),
    build: PromptBuilder.() -> Unit = {}
) = prompt(id, params) {
    system(BASE_SYSTEM_PROMPT)
    if (systemPrompt != null) system(systemPrompt)

    for (m in messages) {
        when (m.role) {
            ChatMessageAuthor.User -> user(m.content)
            ChatMessageAuthor.Agent -> assistant(m.content)
            else -> {}
        }
    }
    build()
}


fun ChatPromptsList.buildPrompt(
    id: String = "chat",
    params: LLMParams = LLMParams(),
    build: PromptBuilder.() -> Unit = {}
) = prompt(id, params) {
    system(BASE_SYSTEM_PROMPT)
    for (m in messages) {
        when (m.role) {
            ChatMessageAuthor.User -> user(m.content)
            ChatMessageAuthor.Agent -> assistant(m.content)
            else -> {}
        }
    }
    build()
}
