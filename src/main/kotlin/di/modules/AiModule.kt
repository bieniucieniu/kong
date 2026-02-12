package com.bieniucieniu.di.modules

import com.bieniucieniu.features.ai.providers.google.GoogleService
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import com.bieniucieniu.features.ai.services.AiService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val aiModules = module {
    single {
        OllamaService(get(named("ollama-http-client")), get())
    }
    single {
        GoogleService(get())
    }
    single {
        AiService(
            listOf(get<OllamaService>(), get<GoogleService>())
                .filter { it.isActive() }
                .associateBy { it.provider.id }
        )
    }
}