package com.bieniucieniu.features.ai.routes

import com.bieniucieniu.auth.getUserSession
import com.bieniucieniu.features.ai.models.ChatSession
import com.bieniucieniu.features.ai.services.ChatService
import com.bieniucieniu.features.shared.models.ErrorResponse
import com.bieniucieniu.features.shared.models.Paginated
import com.bieniucieniu.features.shared.models.pagination
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.koin.ktor.ext.inject
import paginationQueryParams

fun Route.sessionRoutes() {
    val c: ChatService by inject()
    route("sessions") {
        get {
            val p = pagination()
            val search = call.queryParameters["search"]
            val o = suspendTransaction {
                addLogger(StdOutSqlLogger)
                c.getUserChatSessionsList(getUserSession(), p.offset, p.count, search)
            }
            call.respond(p.paginated(o))
        }.describe {
            parameters {
                query("search") {
                    description = "Search"
                    required = false
                }
                paginationQueryParams()
            }
            responses {
                HttpStatusCode.OK {
                    schema = jsonSchema<Paginated<ChatSession>>()
                }
                HttpStatusCode.Unauthorized {
                    description = "Unauthorized"
                    schema = jsonSchema<ErrorResponse>()
                }
            }
        }
    }
}
