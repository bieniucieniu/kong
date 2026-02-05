package com.bieniucieniu.db

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    val url = environment.config.property("database.url").getString()
    val database = environment.config.propertyOrNull("database.database")?.getString() ?: "kong"

    val username = environment.config.property("database.username").getString()
    val password = environment.config.property("database.password").getString()

    Database.connect(
        "jdbc:postgresql://$url/$database",
        driver = "org.postgresql.Driver",
        user = username,
        password = password
    )
}