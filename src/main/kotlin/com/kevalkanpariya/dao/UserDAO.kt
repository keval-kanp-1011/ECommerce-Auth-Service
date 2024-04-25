package com.kevalkanpariya.dao

import com.kevalkanpariya.data.models.User

interface UserDAO {

    suspend fun insertUser(
        name: String,
        email: String,
        pwd: String
    ): User?

    suspend fun fetchUser(
        email: String,
        pwd: String
    ): User?

    suspend fun findUserByEmail(
        email: String
    ): User?


}