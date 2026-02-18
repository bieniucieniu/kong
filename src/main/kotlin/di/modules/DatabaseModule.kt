package com.bieniucieniu.di.modules

import com.bieniucieniu.features.ai.repositories.ChatMessageTable
import com.bieniucieniu.features.ai.repositories.ChatSessionTable
import com.bieniucieniu.features.auth.repositories.UsersTable
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.core.exposedLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.koin.dsl.module

val databaseModule = module {
    single<Database> {
        val config = get<Application>().environment.config
        val url = config.property("database.url").getString()
        val database = config.propertyOrNull("database.database")?.getString() ?: "kong"
        val username = config.property("database.username").getString()
        val password = config.property("database.password").getString()

        Database.connect(
            "jdbc:postgresql://$url/$database",
            driver = "org.postgresql.Driver",
            user = username,
            password = password,
            databaseConfig = DatabaseConfig {
                useNestedTransactions = false
            }
        ).also {
            val tables = arrayOf(
                UsersTable,
                ChatSessionTable,
                ChatMessageTable
            )
            transaction(it) {
                SchemaUtils.create(tables = tables)

                transaction {

                    val statements = MigrationUtils.statementsRequiredForDatabaseMigration(tables = tables)

                    if (statements.isNotEmpty()) {
                        exposedLogger.info("--- Migration Script ---")
                        for (statement in statements) {
                            exposedLogger.info(statement)
                            exec(statement)
                        }
                    } else exposedLogger.info("Database is up to date!")

                }
            }
        }
    }
}