package com.bieniucieniu

import com.bieniucieniu.auth.installAuthPlugins
import com.bieniucieniu.db.installDatabases
import com.bieniucieniu.di.installKoin
import com.bieniucieniu.features.ai.configureAi
import com.bieniucieniu.features.auth.configureAuth
import com.bieniucieniu.routing.installRoutingPlugins
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    installKoin()
    installDatabases()
    installRoutingPlugins()
    installAuthPlugins()

    configureAuth()
    configureAi()
}
