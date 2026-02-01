package com.bieniucieniu

import com.bieniucieniu.di.appModule
import com.bieniucieniu.features.ai.configureAi
import com.bieniucieniu.features.auth.configureAuth
import com.bieniucieniu.plugins.configureRouting
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(Koin) {
        modules(appModule)
    }
    configureRouting()
    configureSerialization()
    configureAuth()
    configureAi()
}
