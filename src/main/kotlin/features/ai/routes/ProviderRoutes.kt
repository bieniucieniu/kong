package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.errors.responses.notFound
import com.bieniucieniu.features.ai.models.SerializableLLMProvider
import com.bieniucieniu.features.ai.models.toSerializableLLMProvider
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.koin.ktor.ext.inject

fun Route.modelProviderRoutes() {
    val s: AiService by inject()
    route("providers") {
        get {
            print("Extracting models...\n")
            val providers = s.getProviders { it.toSerializableLLMProvider() }.takeIf { it.isNotEmpty() }
                ?: throw notFound("No providers found")

            call.respond(providers)
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
            val p = s.getDefaultService()?.provider ?: throw notFound("no default provider")

            call.respond(p.toSerializableLLMProvider())
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
}