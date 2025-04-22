package tables

import models.Product
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Catalog : Table() {
    val id = varchar("id", 50)
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 255)
    val price = integer("price")
    val description = text("description")
    val imageUrls = text("imageUrls")
    
    fun insertProducts(products: List<Product>) {
        transaction {
            batchInsert(products) { product ->
                this[Catalog.id] = product.id
                this[name] = product.name
                this[price] = product.price
                this[description] = product.description
                this[imageUrls] = product.imageUrls.joinToString(",")
            }
        }
    }

    fun insertProduct(id: String, name: String, price: Int, description: String, imageUrls: List<String>) {
        transaction {
            insert {
                it[Catalog.id] = id
                it[Catalog.name] = name
                it[Catalog.price] = price
                it[Catalog.description] = description
                it[Catalog.imageUrls] = imageUrls.joinToString(",")
            }
        }
    }

    fun getAllProducts(): List<ResultRow> = transaction {
        selectAll().toList()
    }
}