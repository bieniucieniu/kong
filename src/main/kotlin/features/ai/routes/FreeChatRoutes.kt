package com.bieniucieniu.features.ai.routes

import ai.koog.ktor.llm
import com.bieniucieniu.errors.responses.badRequest
import com.bieniucieniu.features.ai.models.ChatPromptsList
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.ai.services.buildPrompt
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.bieniucieniu.features.shared.responses.streamFlow
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.koin.ktor.ext.inject

fun Route.freeChatRoutes() {
    val s: AiService by inject()
    route("chat") {
        post("free") {
            val p = call.receiveNullable<ChatPromptsList>() ?: throw badRequest("Missing/Incorrect body")
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
