package com.bieniucieniu.db

import com.bieniucieniu.features.auth.repositories.UsersTable
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.installDatabases() {
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

    transaction {
        SchemaUtils.create(UsersTable)
    }
}