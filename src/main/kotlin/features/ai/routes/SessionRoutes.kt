package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.auth.getUserSession
import com.bieniucieniu.features.ai.models.ChatSession
import com.bieniucieniu.features.ai.services.ChatService
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.koin.ktor.ext.inject

fun Route.sessionRoutes() {
    val c: ChatService by inject()
    route("sessions") {
        get {
            val offset = call.queryParameters["offset"]?.toLong() ?: 0
            val count = call.queryParameters["count"]?.toInt() ?: 20
            val search = call.queryParameters["search"]
            call.respond(suspendTransaction {
                c.getUserChatSessionsList(getUserSession(), offset, count, search)
            })
        }.describe {
            parameters {
                query("search") {
                    description = "Search"
                    required = false
                }
                query("offset") {
                    description = "Offset"
                    required = false
                }
                query("count") {
                    description = "Count"
                    required = false
                }
            }
            responses {
                HttpStatusCode.OK {
                    schema = jsonSchema<List<ChatSession>>()
                }
                HttpStatusCode.Unauthorized {
                    description = "Unauthorized"
                    schema = jsonSchema<ErrorResponse>()
                }
            }
        }
    }
}
