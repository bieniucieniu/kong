package com.bieniucieniu.features.ai.routes

import ai.koog.ktor.llm
import com.bieniucieniu.auth.getUserSession
import com.bieniucieniu.features.ai.models.ChatMessageAuthor
import com.bieniucieniu.features.ai.models.ChatPrompt
import com.bieniucieniu.features.ai.models.ChatPromptsList
import com.bieniucieniu.features.ai.models.ChatSession
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.ai.services.buildPrompt
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.bieniucieniu.features.shared.responses.badRequest
import com.bieniucieniu.features.shared.responses.notFound
import com.bieniucieniu.features.shared.responses.streamFlow
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.chatRoutes() {
    val s: AiService by inject()
    post("chat/new") {
        suspendTransaction {
            val session = s.createChatSession(
                call.queryParameters["name"],
                call.queryParameters["system_prompt"],
                getUserSession()
            )
            call.respond(session)
        }
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
        val o = s.getUserChatSession(
            id = call.parameters["id"]?.let { Uuid.parseHexDash(it) } ?: return@get call.badRequest("Missing id"),
            session = getUserSession(),
            includeMessages = call.queryParameters["include_messages"]?.toBoolean() ?: true
        ) ?: call.notFound("chat session not found")



        call.respond(o)
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
        val id = call.parameters["id"]?.let { Uuid.parseHexDash(it) } ?: return@post call.badRequest("Missing id")
        suspendTransaction {
            val cs = s.getUserChatSession(id, getUserSession())
                ?: return@suspendTransaction call.notFound("Chat session not found")

            val p = call.receive<ChatPrompt>()
            val m = s.getModel(p.model, p.provider) ?: return@suspendTransaction call.badRequest("Model not found")

            s.saveMessage(cs.id, ChatMessageAuthor.User, p.message)
            val f = llm().executeStreaming(cs.buildPrompt { user(p.message) }, m)

            var acc = ""
            call.streamFlow(f) { acc += it }
            s.saveMessage(cs.id, ChatMessageAuthor.Agent, acc)
        }
    }.describe {
        description = "Chat with AI on stable session"
        parameters {
            path("id") {
                description = "Chat session id [Uuid]"
                required = true
            }
        }
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
        val p = call.receive<ChatPromptsList>()
        val m = s.getModel(p.model, p.provider) ?: return@post call.badRequest("Model not found")

        val f = llm().executeStreaming(p.buildPrompt(), m)
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
