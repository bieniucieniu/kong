package com.bieniucieniu.lib.utils

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.scope.Scope


fun ApplicationConfig.isDev(): Boolean = propertyOrNull("ktor.development")?.getAs() ?: false
fun Application.isDev(): Boolean = environment.config.isDev()
fun Scope.isDev(): Boolean = get<Application>().isDev()