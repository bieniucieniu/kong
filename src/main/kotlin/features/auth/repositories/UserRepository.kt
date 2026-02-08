package com.bieniucieniu.features.auth.repositories

import org.jetbrains.exposed.sql.Table

const val MAX_VARCHAR_LENGTH = 255


object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val google_id = varchar("google_id", MAX_VARCHAR_LENGTH)
}