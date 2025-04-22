package com.example.hardwarestore.repository

import android.util.Log
import com.example.hardwarestore.models.Product
import com.example.hardwarestore.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.*


val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

suspend fun authUser(token: String?): Boolean {
    return try {
        val response = client.get("http://10.0.2.2:8080/auth") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }
        response.status == HttpStatusCode.OK
    } catch (e: Exception) {
        false
    }
}

suspend fun loginUser(user: User): String? {
    return try {
        val response: Map<String, String> = client.post("http://10.0.2.2:8080/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
        response["token"]
    } catch (e: Exception) {
        Log.e("Login", "Login error: ${e.localizedMessage}")
        null
    }
}

suspend fun registerUser(user: User): Boolean {
    return try {
        val response: HttpStatusCode = client.post("http://10.0.2.2:8080/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()

        response == HttpStatusCode.Created
    } catch (e: Exception) {
        Log.e("Register", "Registration error: ${e.localizedMessage}")
        false
    }
}

suspend fun getCart(token: String?): List<String> = try {
    client.get("http://10.0.2.2:8080/cart") {
        headers { append(HttpHeaders.Authorization, "Bearer $token") }
    }.body()
} catch (e: Exception) { emptyList() }

suspend fun addToCart(token: String?, productId: String) {
    client.post("http://10.0.2.2:8080/cart/add") {
        headers { append(HttpHeaders.Authorization, "Bearer $token") }
        setBody(productId)
    }
}

suspend fun removeFromCart(token: String?, productId: String) {
    client.post("http://10.0.2.2:8080/cart/remove") {
        headers { append(HttpHeaders.Authorization, "Bearer $token") }
        setBody(productId)
    }
}

suspend fun getCatalog(): List<Product> = try {
    client.get("http://10.0.2.2:8080/catalog").body()
} catch (e: Exception) {
    emptyList()
}