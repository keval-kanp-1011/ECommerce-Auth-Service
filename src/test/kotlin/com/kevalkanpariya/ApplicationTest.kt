package com.kevalkanpariya

import com.kevalkanpariya.models.Articles
import com.kevalkanpariya.plugins.*
import com.sun.tools.javac.util.Log
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testApp() = testApplication {
        environment {
            config = MapApplicationConfig()
        }
        application {
            TestDatabaseSingleton.init()
            configureRouting()
            configureTemplating()
        }

        val client = createClient {
            followRedirects = false
        }

//        val response = client.get("/static/aboutme.html")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertEquals(
//            """
//<!DOCTYPE html>
//<html lang="en">
//<head>
//    <meta charset="UTF-8">
//    <title>Kotlin Journal</title>
//</head>
//<body style="text-align: center; font-family: sans-serif">
//<img src="/static/ktor_logo.png" alt="ktor logo">
//<h1>About me</h1>
//<p>Welcome to my static page!</p>
//<p>Feel free to take a look around.</p>
//<p>Or go to the <a href="/">main page</a>.</p>
//</body>
//</html>
//                    """.trimIndent(),
//            response.bodyAsText()
//        )

        client.get("/articles").let {
            assertEquals(HttpStatusCode.OK, it.status)
            assertTrue { it.bodyAsText().contains("Kotlin Ktor Journal") }
        }

        client.get("/articles/1").let {
            assertEquals(HttpStatusCode.OK, it.status)
            assertTrue { it.bodyAsText().contains("The drive to develop!") }
        }

        var locationHeader = ""
        client.post("/articles") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("title" to "I finished a tutorial!", "body" to "It was great!").formUrlEncode())
        }.let {
            locationHeader = it.headers["Location"]!!
            assertTrue { locationHeader.contains("articles") }
        }

        client.get(locationHeader).let {
            assertTrue { it.bodyAsText().contains("I finished a tutorial!") }
        }
    }
}

object TestDatabaseSingleton {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}