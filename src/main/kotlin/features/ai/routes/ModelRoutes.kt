package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.errors.responses.badRequest
import com.bieniucieniu.errors.responses.noContent
import com.bieniucieniu.errors.responses.notFound
import com.bieniucieniu.features.ai.models.SerializableLLModel
import com.bieniucieniu.features.ai.models.toSerializableLLModel
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.shared.models.ErrorResponse
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
            val provider = call.parameters["provider_id"] ?: throw badRequest("Provider not provided")
            val m = s.getAvailableLLModels(provider)
            if (m.isEmpty()) throw noContent("no content", emptyList<SerializableLLModel>())
            else call.respond(m.map { it.toSerializableLLModel() })
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
            val provider = call.parameters["provider_id"] ?: throw badRequest("Provider not provided")
            val m = s.getDefaultModel(provider)?.toSerializableLLModel()
                ?: throw notFound("Model not found, probably all services are inactive")

            call.respond(m)
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
            val provider = call.parameters["provider_id"] ?: throw badRequest("Provider not provided")
            val name = call.parameters["model_id"] ?: throw badRequest("Model not provided")
            val model = s.getAvailableLLModels(provider).find { it.id == name } ?: throw notFound("Model not found")
            call.respond(model.toSerializableLLModel())
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