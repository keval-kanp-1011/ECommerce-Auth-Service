package com.kevalkanpariya.data.models


import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val accessToken: String? = null
)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String
)

object Users: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val email = varchar("email", 20)
    val password = varchar("password", 100)
    override val primaryKey = PrimaryKey(id)
}
