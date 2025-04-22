package routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import tables.Carts

fun Route.cartRoutes() {
    authenticate {
        
        get("/cart") {
            val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString()
            if (username != null) {
                val cart = Carts.getCart(username)
                call.respond(
                    cart,
                    typeInfo<List<String>>()
                )
            } else {
                call.respond(HttpStatusCode.BadRequest, typeInfo<HttpStatusCode>())
            }
        }

        post("/cart/add") {
            val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString()
            val productId = call.receive<String>()
            if (username != null) {
                Carts.addItem(username, productId)
                call.respond(HttpStatusCode.OK, typeInfo<HttpStatusCode>())
            } else {
                call.respond(HttpStatusCode.BadRequest, typeInfo<HttpStatusCode>())
            }
        }

        post("/cart/remove") {
            val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString()
            val productId = call.receive<String>()
            if (username != null) {
                Carts.removeItem(username, productId)
                call.respond(HttpStatusCode.OK, typeInfo<HttpStatusCode>())
            } else {
                call.respond(HttpStatusCode.BadRequest, typeInfo<HttpStatusCode>())
            }
        }
    }
}