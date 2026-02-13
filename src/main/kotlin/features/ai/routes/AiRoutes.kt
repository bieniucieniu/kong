package com.bieniucieniu.features.ai.routes

import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.streaming.StreamFrame
import com.bieniucieniu.features.ai.models.*
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.shared.models.ErrorResponse
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
    val s: AiService by inject()
    post("chat") {
        assert(s.isActive()) {
            "no active/provided services in ${s.services.keys.joinToString()}"
        }
        val p = call.receive<ChatPrompt>()
        val minChunkSize = 50
        val model =
            s.getAvailableLLModels(p.provider)
                .find { it.id == p.model }
                ?: s.getDefaultModel(p.provider)
        print(model)
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
            model = model ?: run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    message = ErrorResponse("Model not found")
                )
                return@post
            }
        )


        call.respondBytesWriter(contentType = ContentType.Text.EventStream) {
            try {
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
            } catch (e: Throwable) {
                writeByteArray("error: ${e.message}".toByteArray())
            }
        }
    }.describe {
        description = "Chat with AI"
        requestBody {
            schema = jsonSchema<ChatPrompt>()
        }

        responses {
            HttpStatusCode.OK {
                ContentType.Text.EventStream()
            }
        }
    }
    cacheOutput(30.minutes) {
        route("providers") {
            get {
                val providers = s.getProviders { it.toSerializableLLMProvider() }
                if (providers.isEmpty()) call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("No providers found")
                ) else call.respond(providers)

            }.describe {
                description = "Get list of all providers"
                responses {
                    HttpStatusCode.OK {
                        description = "Default provider"
                        schema = jsonSchema<List<SerializableLLMProvider>>()
                    }
                    HttpStatusCode.NotFound {
                        description = "No default provider"
                        schema = jsonSchema<ErrorResponse>()
                    }
                }
            }
            get("default") {
                val p = s.getDefaultProvider()
                if (p == null) call.respond(
                    HttpStatusCode.NoContent,
                    ErrorResponse("no default provider")
                ) else call.respond(p.toSerializableLLMProvider())
            }.describe {
                description = "Get default provider"
                responses {
                    HttpStatusCode.OK {
                        description = "Default provider"
                        schema = jsonSchema<SerializableLLMProvider>()
                    }
                    HttpStatusCode.NotFound {
                        description = "No default provider"
                        schema = jsonSchema<ErrorResponse>()
                    }
                }
            }

        }
        route("models") {

            get("{provider_id}") {
                val provider =
                    call.parameters["provider_id"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Provider not provided")
                    )
                val models = s.getAvailableLLModels(provider)
                if (models.isEmpty())
                    call.respond(
                        HttpStatusCode.NoContent,
                        message = emptyList<SerializableLLModel>()
                    )
                else call.respond(models.map { it.toSerializableLLModel() })
            }.describe {
                description = "Get list of all models"
                responses {
                    HttpStatusCode.OK {
                        description = "List of models"
                        schema = jsonSchema<List<SerializableLLModel>>()
                    }
                    HttpStatusCode.NoContent {
                        description = "No content"
                        schema = jsonSchema<List<SerializableLLModel>>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "Provider not provided"
                        schema = jsonSchema<ErrorResponse>()
                    }
                }
            }
            get("{provider_id}/default") {
                val provider =
                    call.parameters["provider_id"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Provider not provided")
                    )
                s.getDefaultModel(provider)?.toSerializableLLModel()?.let {
                    call.respond(it)
                } ?: call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Model not found, probably all services are inactive")
                )
            }.describe {
                responses {
                    HttpStatusCode.OK {
                        description = "Model found"
                        schema = jsonSchema<SerializableLLModel>()
                    }
                    HttpStatusCode.NotFound {
                        description = "Model not found"
                        schema = jsonSchema<ErrorResponse>()
                    }

                }
            }

            get("{provider_id}/{model_id}") {
                val provider = call.parameters["provider_id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Provider not provided")
                )
                val name = call.parameters["model_id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Model not provided")
                )
                val model = s.getAvailableLLModels(provider).find { it.id == name }
                if (model != null) call.respond(model.toSerializableLLModel())
                else call.respond(
                    HttpStatusCode.NotFound,
                    message = ErrorResponse("Model not found")
                )
            }.describe {
                description = "Get model by name"
                responses {
                    HttpStatusCode.OK {
                        description = "Model found"
                        schema = jsonSchema<SerializableLLModel>()
                    }
                    HttpStatusCode.NotFound {
                        description = "Model not found"
                        schema = jsonSchema<ErrorResponse>()
                    }
                }
            }
        }
    }
}