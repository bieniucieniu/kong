package com.bieniucieniu.di.modules

import com.bieniucieniu.features.auth.services.DiscordService
import com.bieniucieniu.features.auth.services.UserService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun Application.getDiscordModules() = module {
    singleOf(::DiscordService)
    singleOf(::UserService)
}