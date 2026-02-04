package com.bieniucieniu.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.logging.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.getAppModules() = module {
    single(named("ollama-http-client")) {
        val username = environment.config.propertyOrNull("koog.ollama.username")?.getString()
        val password = environment.config.propertyOrNull("koog.ollama.password")?.getString()
        val baseUrlRealm = environment.config.propertyOrNull("koog.ollama.baseUrlRealm")?.getString()
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = null
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            if (!username.isNullOrBlank() && !password.isNullOrBlank())
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
    }
    single {
        HttpClient(CIO) {
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
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }
}