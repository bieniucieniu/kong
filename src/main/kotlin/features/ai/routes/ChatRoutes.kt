package com.bieniucieniu.features.ai.routes

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import com.bieniucieniu.features.ai.models.ChatMessageAuthor
import com.bieniucieniu.features.ai.models.ChatPrompt
import com.bieniucieniu.features.ai.models.ChatPromptsList
import com.bieniucieniu.features.ai.models.ChatSession
import com.bieniucieniu.features.ai.repositories.ChatMessageDao
import com.bieniucieniu.features.ai.repositories.ChatSessionDao
import com.bieniucieniu.features.ai.repositories.ChatSessionTable
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.bieniucieniu.features.shared.responses.*
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.chatRoutes() {
    val s: AiService by inject()
    post("chat/new") {
        val session = call.principal<UserSession>() ?: return@post call.unauthorized()
        val o = suspendTransaction {
            ChatSessionDao.new {
                ownerId = session.userId
                name = call.queryParameters["name"]
                systemPrompt = call.queryParameters["system_prompt"]
            }
        }
        call.respond(o.toChatSession())
    }.describe {
        description = "Create new chat session"

        responses {
            HttpStatusCode.OK {
                schema = jsonSchema<ChatSession>()
            }
            HttpStatusCode.Unauthorized {
                description = "Unauthorized"
                schema = jsonSchema<ErrorResponse>()
            }
        }
    }
    get("chat/{id}") {
        val includeMessages = call.queryParameters["include_messages"]?.toBoolean() ?: true
        val id = call.parameters["id"]?.let {
            Uuid.parseHexDash(it)
        } ?: return@get call.badRequest("Missing id")

        val session = call.principal<UserSession>() ?: return@get call.unauthorized()

        val conn = (ChatSessionTable.id eq id) and (ChatSessionTable.ownerId eq session.userId)
        val s =
            if (includeMessages) ChatSessionDao.find(conn).firstOrNull()?.toChatSession()
            else ChatSessionDao.find(conn).firstOrNull()?.toChatSession()

        if (s != null) call.respond(s)
        else call.notFound("chat session not found")
    }.describe {
        parameters {
            path("id") {
                description = "Chat session id [Uuid]"
                required = true
            }
        }
        responses {
            HttpStatusCode.OK {
                schema = jsonSchema<ChatPromptsList>()
            }
            HttpStatusCode.BadRequest {
                description = "bad request"
                schema = jsonSchema<ErrorResponse>()
            }
            HttpStatusCode.NotFound {
                description = "Chat session not found"
                schema = jsonSchema<ErrorResponse>()
            }
            HttpStatusCode.Unauthorized {
                description = "Unauthorized"
                schema = jsonSchema<ErrorResponse>()
            }
        }
    }
    post("chat/{id}") {
        val id = call.parameters["id"]?.let {
            Uuid.parseHexDash(it)
        } ?: return@post call.badRequest("Missing id")
        val session = call.principal<UserSession>() ?: return@post call.unauthorized()
        val p = call.receive<ChatPrompt>()
        val model = s.getAvailableLLModels(p.provider).find { it.id == p.model } ?: s.getDefaultModel(p.provider)
        val s = suspendTransaction {
            ChatSessionDao.find {
                (ChatSessionTable.id eq id) and (ChatSessionTable.ownerId eq session.userId)
            }.firstOrNull()?.toChatSession(includeMessages = true)
        } ?: return@post call.notFound("chat session not found")


        val f = llm().executeStreaming(
            prompt = prompt("chat") {
                if (s.systemPrompt != null) system(s.systemPrompt)
                else system("You are a helpful assistant. Write short, simple and concise answers")
                for (m in s.messages) {
                    when (m.role) {
                        ChatMessageAuthor.User -> user(m.content)
                        ChatMessageAuthor.Agent -> assistant(m.content)
                        else -> {}
                    }
                }
                user(p.message)
            },
            model = model ?: return@post call.badRequest("Model not found")
        )

        var acc = ""
        call.streamFlow(f) { acc += it }
        suspendTransaction {
            ChatMessageDao.new {
                sessionId = id
                role = ChatMessageAuthor.User
                content = p.message
            }
            ChatMessageDao.new {
                sessionId = id
                role = ChatMessageAuthor.Agent
                content = acc
            }
        }
    }.describe {
        description = "Chat with AI on stable session"
        requestBody {
            schema = jsonSchema<ChatPromptsList>()
        }

        responses {
            HttpStatusCode.OK {
                ContentType.Text.EventStream()
            }
            HttpStatusCode.Unauthorized {
                description = "Unauthorized"
                schema = jsonSchema<ErrorResponse>()
            }
            HttpStatusCode.ServiceUnavailable {
                description = "No active services"
                schema = jsonSchema<ErrorResponse>()
            }
        }
    }
    post("chat") {

        if (!s.isActive())
            return@post call.serviceUnavailable("no active/provided services in ${s.services.keys.joinToString()}")

        val p = call.receive<ChatPromptsList>()
        val model = s.getAvailableLLModels(p.provider).find { it.id == p.model } ?: s.getDefaultModel(p.provider)
        val f = llm().executeStreaming(
            prompt = prompt("chat") {
                system("You are a helpful assistant. Write short, simple and concise answers")
                for (m in p.messages) {
                    when (m.role) {
                        ChatMessageAuthor.User -> user(m.content)
                        ChatMessageAuthor.Agent -> assistant(m.content)
                        else -> {}
                    }
                }
            },
            model = model ?: return@post call.badRequest("Model not found")
        )
        var acc = ""
        call.streamFlow(f) { acc += it }
    }.describe {
        description = "Chat with AI"
        requestBody {
            schema = jsonSchema<ChatPromptsList>()
        }

        responses {
            HttpStatusCode.OK {
                ContentType.Text.EventStream()
            }
            HttpStatusCode.ServiceUnavailable {
                description = "No active services"
                schema = jsonSchema<ErrorResponse>()
            }
        }
    }
}
