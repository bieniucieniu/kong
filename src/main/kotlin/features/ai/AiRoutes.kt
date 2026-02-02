package com.bieniucieniu.features.ai

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.llm.OllamaModels
import ai.koog.prompt.streaming.StreamFrame
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*
import kotlin.reflect.full.staticProperties


val a = GoogleModels::class.staticProperties

@OptIn(ExperimentalKtorApi::class)
fun Route.aiRoutes() {
    route("/ai") {
        post("chat") {
            val p = call.receive<Chat>()
            print(p.messages.last().prompt)
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

                model = OllamaModels.Meta.LLAMA_3_2_3B
            )


            call.respondOutputStream(contentType = ContentType.Text.EventStream) {
                f.collect { chunk ->
                    val str = when (chunk) {
                        is StreamFrame.Append -> chunk.text
                        else -> ""
                    }
                    print("${str.length}:$str")
                    flush()
                    write(str.toByteArray())
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