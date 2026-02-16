package com.bieniucieniu.features.ai.repositories

import com.bieniucieniu.features.ai.models.ChatMessage
import com.bieniucieniu.features.ai.models.ChatMessageAuthor
import com.bieniucieniu.features.ai.models.ChatSession
import com.bieniucieniu.features.auth.repositories.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid

const val MAX_VARCHAR_LENGTH = 255


object ChatSessionTable : UuidTable("chat_session") {
    val ownerId = uuid("owner_id").references(UsersTable.id)
    val name = varchar("name", MAX_VARCHAR_LENGTH).nullable().default(null)
    val systemPrompt = text("system_prompt").nullable().default(null)
}

object ChatMessageTable : UuidTable("chat_message") {
    val sessionId = uuid("session_id").references(ChatSessionTable.id)
    val role = varchar("role", MAX_VARCHAR_LENGTH)
    val content = text("content")
    val createdAt = long("created_at")
}

class ChatSessionDao(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<ChatSessionDao>(ChatSessionTable)

    var ownerId by ChatSessionTable.ownerId
    var name by ChatSessionTable.name
    var systemPrompt by ChatSessionTable.systemPrompt
    val messages by ChatMessageEntity referrersOn ChatMessageTable.sessionId


    fun toChatSession(includeMessages: Boolean = false) = ChatSession(
        id = id.toString(),
        name = name,
        systemPrompt = systemPrompt,
        messages = if (includeMessages) messages.map { it.toChatMessage() } else emptyList()
    )
}

class ChatMessageEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<ChatMessageEntity>(ChatMessageTable)

    var sessionId by ChatMessageTable.sessionId
    var role by ChatMessageTable.role
    var content by ChatMessageTable.content
    var createdAt by ChatMessageTable.createdAt


    fun toChatMessage() = ChatMessage(
        ChatMessageAuthor.fromString(role) ?: ChatMessageAuthor.Agent,
        content,
        createdAt
    )
}
