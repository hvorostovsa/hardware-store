package tables

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Carts : Table() {
    private val username = varchar("username", 50).references(Users.username, onDelete = ReferenceOption.CASCADE)
    private val productId = varchar("productId", 50).references(Catalog.id, onDelete = ReferenceOption.CASCADE)


    fun getCart(username: String): List<String> = transaction {
        select { Carts.username eq username }
            .map { it[productId] }
    }

    fun addItem(username: String, productId: String) {
        transaction {
            insert {
                it[Carts.username] = username
                it[Carts.productId] = productId
            }
        }
    }

    fun removeItem(username: String, productId: String) {
        transaction {
            val items = select { Carts.username eq username and (Carts.productId eq productId) }
                .limit(1)
                .toList()
            if (items.isNotEmpty()) {
                deleteWhere { (Carts.username eq username) and (Carts.productId eq productId) }
            }
        }
    }
}