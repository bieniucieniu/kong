package com.bieniucieniu.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.koin.dsl.module

val appModule = module {
    single { HttpClient(CIO) }
}