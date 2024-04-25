package com.kevalkanpariya.data.repoImpl

import com.kevalkanpariya.dao.UserDAO
import com.kevalkanpariya.data.AuthResponse
import com.kevalkanpariya.data.models.UserResponse
import com.kevalkanpariya.data.repository.AuthRepository
import com.kevalkanpariya.security.JwtConfig
import com.kevalkanpariya.security.hash

class AuthRepoImpl(
    private val userDAO: UserDAO
): AuthRepository {
    override suspend fun registerUser(name: String, email: String, pwd: String): AuthResponse<Any> {
        return try {
            if (isEmailExist(email)) {
                AuthResponse.Success( data = null,message = "user exists")
            } else {
                val user = userDAO.insertUser(name, email, pwd.hash())?: return AuthResponse.Success( data = null,message = "user is not registered")
                AuthResponse.Success(data = UserResponse(
                    id = user.id,
                    name = user.name,
                    password = user.password,
                    email = user.email,
                    accessToken = null
                ), message = "user has been created successfully")
            }
        }catch (e: Exception) {
            AuthResponse.Error(message = "${e.message}")
        }
    }

    override suspend fun loginUser(email: String, pwd: String): AuthResponse<Any> {
        return try {
            val user = userDAO.fetchUser(email, pwd)?: return AuthResponse.Success( data = null,message = "user is not found")
            val token = JwtConfig.instance.createAccessToken(user.id)
            println("xxxxxxxxxxxxxx -- $token, $user")
            AuthResponse.Success(data = UserResponse(
                id = user.id,
                name = user.name,
                password = user.password,
                email = user.email,
                accessToken = token
            ), message = "user has been retrieved")
        } catch (e: Exception) {
            AuthResponse.Error(message = "${e.message}")
        }

    }

    private suspend fun isEmailExist(email: String): Boolean {
        return userDAO.findUserByEmail(email) != null
    }


}