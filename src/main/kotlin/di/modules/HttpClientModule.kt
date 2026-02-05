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
import org.koin.ktor.ext.get

fun Application.getHttpClientModules() = module {
    val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(this@getHttpClientModules.get())
            }
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
    single(named("ollama-http-client")) {
        val username = environment.config.propertyOrNull("koog.ollama.username")?.getString()
        val password = environment.config.propertyOrNull("koog.ollama.password")?.getString()
        val baseUrlRealm = environment.config.propertyOrNull("koog.ollama.baseUrlRealm")?.getString()
        print("credentials $username $password $baseUrlRealm")
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
    single { client }
}