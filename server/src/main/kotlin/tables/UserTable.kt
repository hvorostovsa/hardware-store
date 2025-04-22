package tables

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val username = varchar("username", 50)
    override val primaryKey = PrimaryKey(username)
    val passwordHash = varchar("password_hash", 255)
    private val token = varchar("token", 255).nullable()

    fun insertUser(username: String, passwordHash: String) {
        transaction {
            insert {
                it[Users.username] = username
                it[Users.passwordHash] = passwordHash
            }
        }
    }

    fun getUser(username: String): ResultRow? = transaction {
        select { Users.username eq username }.singleOrNull()
    }

    fun updateToken(username: String, token: String?) {
        transaction {
            update({ Users.username eq username }) {
                it[Users.token] = token
            }
        }
    }
}