package com.bieniucieniu.features.ai.routes

import ai.koog.ktor.llm
import com.bieniucieniu.auth.getUserSession
import com.bieniucieniu.errors.responses.badRequest
import com.bieniucieniu.errors.responses.notFound
import com.bieniucieniu.features.ai.models.*
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.ai.services.ChatService
import com.bieniucieniu.features.ai.services.buildPrompt
import com.bieniucieniu.features.shared.models.ErrorResponse
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
    val c: ChatService by inject()
    val s: AiService by inject()
    route("chat") {
        post("new") {
            suspendTransaction {
                val session = c.createChatSession(
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
        get("{id}/messages") {
            val offset = call.queryParameters["from"]?.toLongOrNull() ?: 0
            val count = call.queryParameters["count"]?.toIntOrNull() ?: 20
            val id = call.parameters["id"]?.let { Uuid.parseHexDash(it) }
                ?: throw badRequest("Missing id")
            val o = suspendTransaction {
                c.getUserChatSession(id, getUserSession())?.let { session ->
                    c.getUserChatSessionMessages(session.id, offset, count).map { it.toChatMessage() }
                }
            } ?: throw notFound("chat session not found")

            call.respond(o)
        }.describe {
            parameters {
                path("id") {
                    description = "Chat session id [Uuid]"
                    required = true
                }
                query("from") {
                    description = "Offset"
                    required = false
                }
                query("count") {
                    description = "Count"
                    required = false
                }
            }
            responses {
                HttpStatusCode.OK {
                    schema = jsonSchema<List<ChatMessage>>()
                }
                HttpStatusCode.BadRequest {
                    description = "Missing id"
                    schema = jsonSchema<ErrorResponse>()
                }
                HttpStatusCode.NotFound {
                    description = "chat session not found"
                    schema = jsonSchema<ErrorResponse>()
                }
            }

        }

        get("{id}") {
            val id = call.parameters["id"]?.let { Uuid.parseHexDash(it) }
                ?: throw badRequest("Missing id")
            val includeMessages = call.queryParameters["include_messages"]?.toBoolean() ?: false
            val o = suspendTransaction {
                c.getUserChatSession(
                    id = id,
                    session = getUserSession(),
                    includeMessages = includeMessages
                )
            } ?: throw notFound("chat session not found")
            call.respond(o)
        }.describe {
            parameters {
                path("id") {
                    description = "Chat session id [Uuid]"
                    required = true
                }
                query("include_messages") {
                    description = "Include messages"
                    required = false
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
        post("{id}") {
            val id = call.parameters["id"]?.let { Uuid.parseHexDash(it) } ?: throw badRequest("Missing id")
            suspendTransaction {
                val cs = c.getUserChatSession(id, getUserSession(), includeMessages = true)
                    ?: throw notFound("Chat session not found")

                val p = call.receive<ChatPrompt>()
                val m = s.getModel(p.model, p.provider) ?: throw badRequest("Model not found")

                print("messages: ${cs.messages}")
                val f = llm().executeStreaming(cs.buildPrompt { user(p.message) }, m)

                c.saveMessage(cs.id, ChatMessageAuthor.User, p.message)
                var acc = ""
                call.streamFlow(f) { acc += it }
                c.saveMessage(cs.id, ChatMessageAuthor.Agent, acc)
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
                schema = jsonSchema<ChatPrompt>()
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
        post {
            val p = call.receive<ChatPromptsList>()
            val m = s.getModel() ?: throw badRequest("Model not found")
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
}
