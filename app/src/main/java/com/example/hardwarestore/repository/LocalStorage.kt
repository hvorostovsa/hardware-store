package com.example.hardwarestore.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.hardwarestore.models.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object LocalStorage {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val CATALOG_KEY = stringPreferencesKey("catalog")

    suspend fun saveUserData(context: Context, token: String, username: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USERNAME_KEY] = username
        }
    }

    fun getUserData(context: Context): Flow<Pair<String?, String?>> =
        context.dataStore.data.map { prefs ->
            val token = prefs[TOKEN_KEY]
            val username = prefs[USERNAME_KEY]
            token to username
        }

    suspend fun clearUserData(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USERNAME_KEY)
        }
    }

    suspend fun saveCatalog(context: Context, catalog: List<Product>) {
        val json = Json.encodeToString(catalog)
        context.dataStore.edit { prefs -> prefs[CATALOG_KEY] = json }
    }

    fun getCatalog(context: Context): Flow<List<Product>> =
        context.dataStore.data.map { prefs ->
            prefs[CATALOG_KEY]?.let { Json.decodeFromString(it) } ?: emptyList()
        }
}
