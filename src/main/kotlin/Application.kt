package com.bieniucieniu

import com.bieniucieniu.di.configureKoin
import com.bieniucieniu.features.ai.configureAi
import com.bieniucieniu.features.auth.configureAuth
import com.bieniucieniu.plugins.configureRouting
import com.bieniucieniu.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureRouting()
    configureAuth()
    configureAi()
}
