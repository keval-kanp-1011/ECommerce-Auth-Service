package com.kevalkanpariya.plugins

import com.kevalkanpariya.security.JwtConfig
import com.kevalkanpariya.security.UserIdPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain

    val jwtSecret = "secret"
    JwtConfig.initialize(jwtSecret)
    authentication {
        jwt {
            realm = JwtConfig.realm

            verifier(JwtConfig.instance.verifier)

            validate { credential ->
                val claim = credential.payload.getClaim(JwtConfig.CLAIM).asInt()
                if (claim != null) UserIdPrincipal(claim) else null
            }

            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is either invalid or expired."
                )
            }
        }
    }

}
