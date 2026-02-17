package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.features.ai.models.SerializableLLMProvider
import com.bieniucieniu.features.ai.models.toSerializableLLMProvider
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.bieniucieniu.features.shared.responses.noContent
import com.bieniucieniu.features.shared.responses.notFound
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
            val providers = s.getProviders { it.toSerializableLLMProvider() }
            if (providers.isEmpty()) call.notFound("No providers found")
            else call.respond(providers)
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
            if (p == null) call.noContent("no default provider")
            else call.respond(p.toSerializableLLMProvider())
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