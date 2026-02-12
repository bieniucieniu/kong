package com.bieniucieniu.di.modules

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val httpClientModules = module {
    single(named("ollama-http-client")) {
        val client: HttpClient = get()
        val application: Application = get()
        val username = application.environment.config.propertyOrNull("ai.ollama.username")?.getString()
        val password = application.environment.config.propertyOrNull("ai.ollama.password")?.getString()
        val baseUrlRealm = application.environment.config.propertyOrNull("ai.ollama.baseUrlRealm")?.getString()
        if (username != null && password != null)
            client.config {
                install(Auth) {
                    basic {
                        credentials {
                            BasicAuthCredentials(
                                username = username,
                                password = password
                            )
                        }
                        if (!baseUrlRealm.isNullOrBlank()) realm = baseUrlRealm
                    }
                }
            }
        else client
    }
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(get()) }
            install(HttpTimeout) {
                requestTimeoutMillis = null
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
}