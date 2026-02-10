package com.bieniucieniu.features.auth.routes

import com.bieniucieniu.features.auth.models.OAuth2Provider
import com.bieniucieniu.features.auth.models.User
import com.bieniucieniu.features.auth.models.UserSession
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val client by inject<HttpClient>()
    authenticate("auth-session") {
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

            post("logout") {
                val s = call.principal<UserSession>("auth-session")
                if (s != null) {
                    call.sessions.clear("auth-session")
                    val res = when (s.provider) {
                        OAuth2Provider.Google -> client.get("https://oauth2.googleapis.com/revoke") {
                            parameter("token", s.accessToken)
                        }

                        OAuth2Provider.Discord -> client.get("https://discord.com/api/oauth2/token/revoke") {
                            parameter("token", s.accessToken)
                        }

                        null -> null
                    }
                    if (res != null && res.status.value !in 200..299)
                        call.respond(res.status, ErrorResponse("Failed to revoke session for ${s.provider?.name}"))
                    else call.respond(HttpStatusCode.OK)
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
