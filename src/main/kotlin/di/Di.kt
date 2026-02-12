package com.bieniucieniu.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.installKoin() {
    install(Koin) {
        modules(getAppModules())
    }
}