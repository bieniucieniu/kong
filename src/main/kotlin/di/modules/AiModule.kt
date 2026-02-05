package com.bieniucieniu.di.modules

import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.getAiModules() = module {
    factory {
        OllamaService(get(named("ollama-http-client")), this@getAiModules)
    }
}