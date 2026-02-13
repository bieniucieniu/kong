package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.auth.services.UserService
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    authenticate("auth-session") {
        val userService: UserService by inject()
        route("users") {
            get("session") {
                val s = call.principal<UserSession>("auth-session")
                val u = s?.let { userService.getUserBySession(it) }
                if (u != null) call.respond(u)
                else if (s != null) call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
                else call.respond(HttpStatusCode.Unauthorized, ErrorResponse("User does not exist"))
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

            post("logout") {
                call.principal<UserSession>("auth-session")?.let {
                    val res = userService.callRevokeUser(it)
                    when (res?.status?.value) {
                        in 200..299 -> {
                            call.sessions.clear<UserSession>()
                            call.respond(HttpStatusCode.OK)
                        }

                        else -> call.respond(
                            res?.status ?: HttpStatusCode.InternalServerError,
                            ErrorResponse("Failed to revoke session for ${it.provider?.name}")
                        )
                    }

                }
            }.describe {
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<Unit>()
                    }
                    HttpStatusCode.Unauthorized {
                        schema = jsonSchema<ErrorResponse>()
                    }
                    default {
                        schema = jsonSchema<ErrorResponse>()
                    }
                }
            }
        }
    }
}
