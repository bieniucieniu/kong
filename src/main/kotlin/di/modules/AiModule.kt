package com.bieniucieniu.di.modules

import com.bieniucieniu.features.ai.providers.google.GoogleService
import com.bieniucieniu.features.ai.providers.ollama.OllamaService
import com.bieniucieniu.features.ai.services.AiService
import com.bieniucieniu.features.ai.services.ChatService
import org.koin.dsl.module


val aiModules = module {
    single { GoogleService(get()) }
    single { OllamaService(get(OLLAMA_HTTP_CLIENT_QUALIFIER), get()) }
    single { AiService(listOf(get<OllamaService>(), get<GoogleService>()).filter { it.isActive() }) }
    single { ChatService() }
}