package com.bieniucieniu.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single { HttpClient(CIO) }
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }
}