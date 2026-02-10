package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.repositories.UserEntity
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Route.userRoutes() {
    route("users") {
        get {
            val users = transaction {
                UserEntity.all().toList().map { it.toUser() }
            }
            call.respond(users)
        }.describe {
            responses {
                HttpStatusCode.OK {
                    schema = jsonSchema<User>()
                }
                HttpStatusCode.NotFound {
                    schema = jsonSchema<ErrorResponse>()
                }
            }
        }
        get("session") {

        }
    }
}