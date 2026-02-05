package com.bieniucieniu.features.ai

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.streaming.StreamFrame
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import com.bieniucieniu.features.ai.providers.ollama.getDefaultModel
import com.bieniucieniu.features.shared.response.ErrorResponse
import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes


@OptIn(ExperimentalKtorApi::class)
fun Route.aiRoutes() {
    val ollamaService: OllamaService by inject()
    route("ai") {
        cacheOutput(30.minutes) {
            get("/models") {
                val models = ollamaService.getAvailableLLModels()
                if (models.isEmpty())
                    call.respond(
                        HttpStatusCode.NoContent,
                        message = emptyList<LLModel>()
                    )
                else
                    call.respond(models)
            }.describe {
                description = "Get list of all models"
                responses {
                    HttpStatusCode.OK {
                        description = "List of models"
                        schema = jsonSchema<List<LLModel>>()
                    }
                    HttpStatusCode.NoContent {
                        description = "No content"
                        schema = jsonSchema<List<LLModel>>()
                    }
                }
            }


            get("/model/{id}") {
                val name = call.parameters["id"]
                val model = ollamaService.getAvailableLLModels().find { it.id == name }
                if (model != null) call.respond(model)
                else call.respond(
                    HttpStatusCode.NotFound,
                    message = ErrorResponse("Model not found")
                )
            }.describe {
                description = "Get model by name"
                responses {
                    HttpStatusCode.OK {
                        description = "Model found"
                        schema = jsonSchema<LLModel>()
                    }
                    HttpStatusCode.NotFound {
                        description = "Model not found"
                        schema = jsonSchema<ErrorResponse>()
                    }
                }
            }
        }


        /**
         * Body: [Chat]
         * Response:
         *  - 200 text/event-stream
         */
        post("chat") {
            val p = call.receive<Chat>()
            val minChunkSize = 50
            val model =
                p.model?.let { model ->
                    ollamaService.getAvailableLLModels().find { it.id == model }
                }?.toLLModel() ?: getDefaultModel()
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
            description = "Chat with AI"
            requestBody {
                schema = jsonSchema<Chat>()
            }

            responses {
                HttpStatusCode.OK {
                    ContentType.Text.EventStream()
                }
            }
        }
    }
}