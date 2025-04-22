package com.example.hardwarestore.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hardwarestore.screens.CartScreen
import com.example.hardwarestore.screens.CatalogScreen
import com.example.hardwarestore.screens.FrontScreen
import com.example.hardwarestore.screens.LoginScreen
import com.example.hardwarestore.screens.ProductScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController,
        startDestination = "front",
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("front") { FrontScreen(navController) }
        composable("catalog") { CatalogScreen(navController) }
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductScreen(productId = productId, navController = navController)
        }
        composable("cart") { CartScreen(navController) }
        composable("login") { LoginScreen(navController) }
    }
}