package com.kevalkanpariya.data

import io.ktor.http.*



sealed class AuthResponse<T>(
    open val statusCode: HttpStatusCode,
    open val data: T? = null,
    open val message: String?= null,
    open val accessToken: String? = null
) {

    data class Success<T>(
        override val data: T?,
        override val accessToken: String? = null,
        override val message: String? = null,
        override val statusCode: HttpStatusCode = HttpStatusCode.OK
    ) : AuthResponse<T>(statusCode, data, message, accessToken)


    data class Error(
        override val message: String,
        override val statusCode: HttpStatusCode = HttpStatusCode.BadRequest
    ) : AuthResponse<Any>(statusCode,  null, message, null)
}