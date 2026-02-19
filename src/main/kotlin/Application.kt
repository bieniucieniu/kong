package com.bieniucieniu

import com.bieniucieniu.auth.installAuthPlugins
import com.bieniucieniu.di.installKoin
import com.bieniucieniu.features.ai.configureAi
import com.bieniucieniu.features.auth.configureAuth
import com.bieniucieniu.routing.installRoutingPlugins
import io.ktor.server.application.*
import io.ktor.server.cio.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.ktor.ext.get

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    installKoin()
    // ensure db is created before anything else
    get<Database>()
    installRoutingPlugins()
    installAuthPlugins()

    configureAuth()
    configureAi()
}