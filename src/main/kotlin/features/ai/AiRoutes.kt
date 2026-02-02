package com.bieniucieniu.features.ai

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.streaming.StreamFrame
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*


@OptIn(ExperimentalKtorApi::class)
fun Route.aiRoutes() {
    route("/ai") {
        post("chat") {
            val p = call.receive<Chat>()
            val f = llm().executeStreaming(
                prompt = prompt("chat") {
                    system("You are a helpful assistant that clarifies questions with as long as possible answer")
                    for (m in p.messages) {
                        when (m.author) {
                            ChatMessageAuthor.User -> user(m.prompt)
                            ChatMessageAuthor.Agent -> assistant(m.prompt)
                            else -> {}
                        }
                    }
                },
                model = GoogleModels.Gemini2_5Flash
            )

            call.respondOutputStream(contentType = ContentType.Text.EventStream) {
                f.collect { chunk ->
                    val str = when (chunk) {
                        is StreamFrame.Append -> "a: ${chunk.text}"
                        is StreamFrame.End -> ""
                        is StreamFrame.ToolCall -> "t: ```\n$chunk\n```"
                    }
                    println(str)
                    write(str.toByteArray())
                    flush()
                }
            }
        }.describe {
            requestBody {
                schema = jsonSchema<Chat>()
            }
            responses {
                HttpStatusCode.OK {
                    ContentType.Text.EventStream
                }
            }
        }
    }
}