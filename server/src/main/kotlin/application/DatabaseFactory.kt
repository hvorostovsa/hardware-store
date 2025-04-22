package application

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tables.*
import kotlinx.serialization.json.*
import models.Product
import java.io.File

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/hardwaredb"
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = "123"
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }

    fun createTables() {
        transaction {
            SchemaUtils.create(Users, Catalog, Carts)
        }
    }

    fun loadCatalog() {
        val jsonFile = File("src/main/resources/catalog.json")
        if (!jsonFile.exists()) return

        val jsonText = jsonFile.readText()
        val products = Json.decodeFromString<List<Product>>(jsonText)
        Catalog.insertProducts(products)
    }

}