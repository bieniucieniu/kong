package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.features.ai.models.SerializableLLModel
import com.bieniucieniu.features.ai.models.toSerializableLLModel
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.bieniucieniu.features.shared.responses.badRequest
import com.bieniucieniu.features.shared.responses.noContent
import com.bieniucieniu.features.shared.responses.notFound
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.koin.ktor.ext.inject

fun Route.modelRoutes() {
    val s: AiService by inject()
    route("models") {
        get("{provider_id}") {
            val provider = call.parameters["provider_id"] ?: return@get call.badRequest("Provider not provided")
            val models = s.getAvailableLLModels(provider)
            if (models.isEmpty()) call.noContent(emptyList<SerializableLLModel>())
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
            val provider = call.parameters["provider_id"] ?: return@get call.badRequest("Provider not provided")
            s.getDefaultModel(provider)?.toSerializableLLModel()?.let {
                call.respond(it)
            } ?: call.notFound("Model not found, probably all services are inactive")
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
            val provider = call.parameters["provider_id"] ?: return@get call.badRequest("Provider not provided")
            val name = call.parameters["model_id"] ?: return@get call.badRequest("Model not provided")
            val model = s.getAvailableLLModels(provider).find { it.id == name }
            if (model != null) call.respond(model.toSerializableLLModel())
            else call.notFound("Model not found")
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