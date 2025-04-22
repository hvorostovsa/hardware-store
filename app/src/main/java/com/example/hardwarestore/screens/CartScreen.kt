package com.example.hardwarestore.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hardwarestore.repository.LocalStorage
import com.example.hardwarestore.repository.addToCart
import com.example.hardwarestore.repository.getCart
import com.example.hardwarestore.repository.getCatalog
import com.example.hardwarestore.models.Product
import com.example.hardwarestore.repository.removeFromCart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var cartItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var token by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        LocalStorage.getUserData(context).collect { user ->
            token = user.first
            if (token != null) {
                cartItems = getCart(token)
            }
        }
    }

    var catalog by remember { mutableStateOf<List<Product>>(emptyList()) }
    LaunchedEffect(Unit) {
        catalog = getCatalog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cart",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF5722),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Your cart is empty",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }
            } else {
                val cartItemCounts = cartItems.groupingBy { it }.eachCount()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 100.dp),
                    contentPadding = PaddingValues(top = 16.dp)
                ) {
                    items(cartItemCounts.keys.toList()) { productId ->
                        val product = catalog.find { it.id == productId }
                        val quantity = cartItemCounts[productId] ?: 0
                        Log.d("CartScreen", "ProductID: $productId, Quantity: $quantity, Product: ${product?.name}, Price: ${product?.price}")
                        product?.let {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { navController.navigate("product/${product.id}") },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(8.dp),
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${product.name} - ${product.price * quantity} rub.",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "x$quantity",
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    addToCart(token, product.id)
                                                    cartItems = getCart(token)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Add",
                                                tint = Color(0xFFFF5722)
                                            )
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                removeFromCart(token, product.id)
                                                cartItems = getCart(token)
                                            }
                                        },
                                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFFF5722)
                                        )
                                    }
                                }
                            }
                            Divider(color = Color(0xFF757575), thickness = 1.dp)
                        }
                    }
                    item {
                        var total = 0
                        cartItemCounts.forEach { (id, quantity) ->
                            val product = catalog.find { it.id == id }
                            if (product != null) {
                                val itemTotal = product.price * quantity
                                Log.d("CartScreen", "Total for $id: $quantity * ${product.price} = $itemTotal")
                                total += itemTotal
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Total: $total rub",
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        cartItems.forEach { productId ->
                                            removeFromCart(token, productId)
                                        }
                                        cartItems = getCart(token)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFFF5722)
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                            ) {
                                Text(
                                    "Clear Cart",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { /* Ничего не делает */ },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFFF5722)
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                            ) {
                                Text(
                                    "Buy",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("catalog") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF5722)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                ) {
                    Text(
                        "Go to Catalog",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedButton(
                    onClick = { navController.navigate("front") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF5722)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                ) {
                    Text(
                        "Go to Home",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}