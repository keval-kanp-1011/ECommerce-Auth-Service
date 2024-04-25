package com.kevalkanpariya.data.repository

import com.kevalkanpariya.data.AuthResponse

interface AuthRepository {

    suspend fun registerUser(name: String, email: String, pwd: String): AuthResponse<Any>
    suspend fun loginUser(email: String, pwd: String): AuthResponse<Any>
}