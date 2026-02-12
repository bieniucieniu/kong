package com.bieniucieniu.di

import com.bieniucieniu.di.modules.getAiModules
import com.bieniucieniu.di.modules.getDiscordModules
import com.bieniucieniu.di.modules.getHttpClientModules
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun Application.getAppModules() = module {
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }
    includes(getHttpClientModules(), getAiModules(), getDiscordModules())
}

