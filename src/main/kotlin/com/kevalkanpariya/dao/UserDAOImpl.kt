package com.kevalkanpariya.dao

import com.kevalkanpariya.dao.DatabaseSingleton.dbQuery
import com.kevalkanpariya.data.models.User
import com.kevalkanpariya.data.models.Users
import org.jetbrains.exposed.sql.*
import java.util.logging.LogRecord
import java.util.logging.Logger


class UserDAOImpl: UserDAO {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        email = row[Users.email],
        password = row[Users.password]
    )
    override suspend fun insertUser(name: String, email: String, pwd: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.password] = pwd
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)

    }

    override suspend fun fetchUser(email: String, pwd: String): User? = dbQuery {
        val d = Users
            .select { Users.email eq email }
            //.selectAll()
            .map(::resultRowToUser)
            .singleOrNull()

        Logger.getGlobal().config("hhhhhhhhhhh")
        println("inside dddbbq, $d")

        d


    }

    override suspend fun findUserByEmail(email: String): User? = dbQuery {

        Users
            .select { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    companion object {
        val dao: UserDAO = UserDAOImpl()
    }
}