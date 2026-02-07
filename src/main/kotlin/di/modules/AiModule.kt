package com.bieniucieniu.di.modules

import com.bieniucieniu.features.ai.AiService
import com.bieniucieniu.features.ai.providers.google.GoogleService
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.getAiModules() = module {
    single {
        OllamaService(get(named("ollama-http-client")), this@getAiModules)
    }
    single {
        GoogleService(this@getAiModules)
    }
    single {
        AiService(
            listOf(get<OllamaService>(), get<GoogleService>())
                .filter { it.isActive() }
                .associateBy { it.name }
        )
    }
}