package com.bieniucieniu

import com.bieniucieniu.db.configureDatabases
import com.bieniucieniu.di.configureKoin
import com.bieniucieniu.features.ai.configureAi
import com.bieniucieniu.features.auth.configureAuth
import com.bieniucieniu.plugins.configureRoutingPlugins
import com.bieniucieniu.plugins.configureStaticResources
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureDatabases()
    configureRoutingPlugins()
    configureAuth()
    configureAi()
    configureStaticResources()
}
