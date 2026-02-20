package com.bieniucieniu.di.modules

import com.bieniucieniu.features.ai.providers.google.GoogleService
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import com.bieniucieniu.features.ai.providers.shared.AiProviderService
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.ai.services.ChatService
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val aiModules = module {
    single { OllamaService(get(named("ollama-http-client")), get()) }.bind<AiProviderService>()
    single { GoogleService(get()) }.bind<AiProviderService>()
    single { AiService(getAll<AiProviderService>()) }
    single { ChatService() }
}