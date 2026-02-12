package com.bieniucieniu.di.modules

import com.bieniucieniu.features.auth.services.DiscordService
import com.bieniucieniu.features.auth.services.GoogleService
import com.bieniucieniu.features.auth.services.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val discordModules = module {
    singleOf(::DiscordService)
    singleOf(::GoogleService)
    singleOf(::UserService)
}