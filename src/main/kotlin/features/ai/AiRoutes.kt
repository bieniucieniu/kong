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
import kotlinx.coroutines.runBlocking
import kotlin.reflect.full.staticProperties


val a = GoogleModels::class.staticProperties

@OptIn(ExperimentalKtorApi::class)
fun Route.aiRoutes() {
    route("/ai") {
        get("models") {

        }
        post("chat") {
            val p = call.receive<Chat>()
            val f = llm().executeStreaming(
                prompt = prompt("chat") {
                    for (m in p.messages) {
                        when (m.author) {
                            ChatMessageAuthor.User -> user(m.prompt)
                            ChatMessageAuthor.Agent -> assistant(m.prompt)
                            else -> {}
                        }
                    }
                },
                model = GoogleModels.Gemini2_0Flash
            )


            call.respondOutputStream(contentType = ContentType.Text.EventStream) {
                runCatching {
                    f.collect { chunk ->
                        val str = when (chunk) {
                            is StreamFrame.Append -> "a: ${chunk.text}"
                            is StreamFrame.End -> ""
                            is StreamFrame.ToolCall -> "t: ```\n$chunk\n```"
                        }
                        println(str)
                        runBlocking {
                            write(str.toByteArray())
                            flush()
                        }
                    }
                }.onFailure { e ->
                    write("e: ${e.message}".toByteArray())
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