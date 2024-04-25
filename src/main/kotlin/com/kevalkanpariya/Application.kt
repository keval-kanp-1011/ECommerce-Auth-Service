package com.kevalkanpariya

import com.kevalkanpariya.dao.DatabaseSingleton
import com.kevalkanpariya.plugins.*
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val server = embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)

    val consulClient = Consul.builder().withUrl("http://localhost:8500").build()
    val service = ImmutableRegistration.builder()
        .id("auth-${server.environment.connectors[0].port}")
        .name("auth-service")
        .address("localhost")
        .port(server.environment.connectors[0].port)
        .build()
    consulClient.agentClient().register(service)
        server.start(wait = true)
}

fun Application.module() {
    DatabaseSingleton.init()
    configureSecurity()
    configureSerialization()
    configureRouting()
    configureTemplating()
}
