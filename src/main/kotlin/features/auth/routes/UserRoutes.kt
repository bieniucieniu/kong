package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*

fun Route.userRoutes() {
    route("users") {
        get("session") {
            val s = call.principal<UserSession>("auth-session")
            if (s != null) call.respond(s)
            else call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
        }.describe {
            responses {
                HttpStatusCode.OK {
                    schema = jsonSchema<User>()
                }
                HttpStatusCode.Unauthorized {
                    schema = jsonSchema<ErrorResponse>()
                }
            }
        }

    }
}