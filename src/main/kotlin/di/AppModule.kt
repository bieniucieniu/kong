package com.bieniucieniu.di

import com.bieniucieniu.di.modules.aiModules
import com.bieniucieniu.di.modules.discordModules
import com.bieniucieniu.di.modules.httpClientModules
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun Application.getAppModules() = module {
    single { this@getAppModules }
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }
    includes(httpClientModules, aiModules, discordModules)
}

