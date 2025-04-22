package routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import models.User
import org.mindrot.jbcrypt.BCrypt
import tables.Users
import utils.TokenManager.generateToken

fun Route.authRoutes() {

    post("/register") {
        val credentials = call.receive<User>()
        val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())

        if (Users.getUser(credentials.username) != null) {
            call.respond(HttpStatusCode.Conflict, typeInfo<HttpStatusCode>())
        } else {
            Users.insertUser(credentials.username, hashedPassword)
            call.respond(HttpStatusCode.Created, typeInfo<HttpStatusCode>())
        }
    }

    post("/login") {
        val credentials = call.receive<User>()
        val user = Users.getUser(credentials.username)

        if (user != null && BCrypt.checkpw(credentials.password, user[Users.passwordHash])) {
            val token = generateToken(credentials.username)
            Users.updateToken(credentials.username, token)
            call.respond(mapOf("token" to token), typeInfo<Map<String, String>>())
        } else {
            call.respond(HttpStatusCode.Unauthorized, typeInfo<HttpStatusCode>())
        }
    }

    authenticate {
        get("/auth") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.payload?.getClaim("username")?.asString()
            if (username != null) {
                call.respond(HttpStatusCode.OK, typeInfo<HttpStatusCode>())
            } else call.respond(HttpStatusCode.Unauthorized, typeInfo<HttpStatusCode>())
        }
    }
}