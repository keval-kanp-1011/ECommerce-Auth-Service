package com.kevalkanpariya.plugins

import com.kevalkanpariya.dao.UserDAOImpl
import com.kevalkanpariya.dao.dao
import com.kevalkanpariya.data.repoImpl.AuthRepoImpl
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {
    routing {

        val userDao = UserDAOImpl()
        val authRepo = AuthRepoImpl(userDao)

        staticResources("/static", "files")

        get("/") {
            call.respondRedirect("articles")
        }

        post("/register") {
            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")
            val email = formParameters.getOrFail("email")
            val pwd = formParameters.getOrFail("password")

            val result = authRepo.registerUser(name, email, pwd)
            call.respond(result.data?: "   ")


        }

        post("/login") {
            val formParameters = call.receiveParameters()
            val email = formParameters.getOrFail("email")
            val pwd = formParameters.getOrFail("password")

            val result = authRepo.loginUser(email, pwd)
            call.respond(result.data?: "  ")
        }

        authenticate {
            route("articles") {
                get {
                    call.respond(FreeMarkerContent("index.ftl", mapOf("articles" to dao.allArticles())))
                }
                get("new") {
                    call.respond(FreeMarkerContent("new.ftl", model = null))
                }
                post {
                    val formParameters = call.receiveParameters()
                    val title = formParameters.getOrFail("title")
                    val body = formParameters.getOrFail("body")
                    val article = dao.addNewArticle(title, body)
                    call.respondRedirect("/articles/${article?.id}")
                }
                get("{id}") {
                    val id = call.parameters.getOrFail<Int>("id").toInt()
                    call.respond(FreeMarkerContent("show.ftl", mapOf("article" to dao.article(id))))
                }
                get("{id}/edit") {
                    val id = call.parameters.getOrFail<Int>("id").toInt()
                    call.respond(FreeMarkerContent("edit.ftl", mapOf("article" to dao.article(id))))
                }
                post("{id}") {
                    val id = call.parameters.getOrFail<Int>("id").toInt()
                    val formParameters = call.receiveParameters()
                    when (formParameters.getOrFail("_action")) {
                        "update" -> {
                            val title = formParameters.getOrFail("title")
                            val body = formParameters.getOrFail("body")
                            dao.editArticle(id, title, body)
                            call.respondRedirect("/articles/$id")
                        }
                        "delete" -> {
                            dao.deleteArticle(id)
                            call.respondRedirect("/articles")
                        }
                    }
                }
            }
        }


    }
}
