package com.bieniucieniu.features.ai

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.streaming.StreamFrame
import com.bieniucieniu.features.ai.providers.ollama.getDefaultModel
import com.bieniucieniu.features.ai.providers.ollama.getOssModels
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
        get("/models") {
            call.respond(
                AiModels(
                    models = getOssModels().keys.toList(),
                    default = getDefaultModel().id
                )
            )
        }

        post("chat") {
            val p = call.receive<Chat>()
            val ossModels = getOssModels()
            val minChunkSize = 50
            val model = p.model?.let { ossModels[it] } ?: getDefaultModel()
            val f = llm().executeStreaming(
                prompt = prompt("chat") {
                    system("You are a helpful assistant. Write short, simple and concise answers")
                    for (m in p.messages) {
                        when (m.author) {
                            ChatMessageAuthor.User -> user(m.prompt)
                            ChatMessageAuthor.Agent -> assistant(m.prompt)
                            else -> {}
                        }
                    }
                },
                model = model
            )


            call.respondBytesWriter(contentType = ContentType.Text.EventStream) {
                var acc = " "
                val writeAcc = suspend {
                    writeByteArray(acc.toByteArray())
                    flush()
                    acc = ""
                }
                writeAcc()
                f.collect { chunk ->
                    val str = when (chunk) {
                        is StreamFrame.Append -> chunk.text
                        else -> ""
                    }
                    acc += str
                    if (acc.length > minChunkSize) writeAcc()
                }
                writeAcc()
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