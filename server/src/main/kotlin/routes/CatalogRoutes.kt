package routes

import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import models.Product
import tables.Catalog

fun Route.catalogRoutes() {
    get("/catalog") {
        val products = Catalog.getAllProducts().map {
            Product(
                id = it[Catalog.id],
                name = it[Catalog.name],
                price = it[Catalog.price],
                description = it[Catalog.description],
                imageUrls = it[Catalog.imageUrls].split(",")
            )
        }
        call.respond(
            products,
            typeInfo<List<Product>>()
        )
    }
}