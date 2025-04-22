package application

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import routes.authRoutes
import routes.cartRoutes
import routes.catalogRoutes
import utils.TokenManager.verifier

fun Application.module() {

    DatabaseFactory.init()
    DatabaseFactory.createTables()
    DatabaseFactory.loadCatalog()

    install(ContentNegotiation) { json() }
    install(AutoHeadResponse)
    install(Authentication) {
        jwt {
            realm = "server"
            verifier(verifier)
            validate { credential ->
                if (credential.payload.getClaim("username")
                        .asString() != null
                ) JWTPrincipal(credential.payload) else null
            }
        }
    }

    routing {
        staticResources("/images", "images")
        authRoutes()
        catalogRoutes()
        cartRoutes()
    }
}
