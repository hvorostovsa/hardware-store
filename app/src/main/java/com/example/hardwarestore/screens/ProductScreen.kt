package com.example.hardwarestore.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hardwarestore.repository.LocalStorage
import com.example.hardwarestore.repository.addToCart
import com.example.hardwarestore.repository.getCart
import com.example.hardwarestore.repository.getCatalog
import com.example.hardwarestore.models.Product
import com.example.hardwarestore.repository.removeFromCart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(productId: String?, navController: NavController) {
    val scope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf<List<String>>(emptyList()) }
    var token by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        LocalStorage.getUserData(context).collect { user ->
            token = user.first
            if (token != null) {
                cartItems = getCart(token)
            }
        }
    }

    var product by remember { mutableStateOf<Product?>(null) }
    LaunchedEffect(productId) {
        val catalog = getCatalog()
        product = catalog.find { it.id == productId }
    }

    product?.let {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            product!!.name,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .background(Color(0xFFF5F5F5)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product!!.imageUrls.firstOrNull())
                            .build(),
                        contentDescription = product!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(BorderStroke(2.dp, Color(0xFF757575)))
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    product!!.name,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Text(
                    "${product!!.price} rub",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Divider(
                    color = Color(0xFF757575),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Text(
                    product!!.description,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    "In Cart: ${cartItems.count { it == product!!.id }}",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            if (token == null) navController.navigate("login")
                            else scope.launch {
                                addToCart(token, product!!.id)
                                cartItems = getCart(token)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF5722)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                    ) {
                        Text(
                            "Add to Cart",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = {
                            if (token != null && cartItems.contains(product!!.id)) {
                                scope.launch {
                                    removeFromCart(token, product!!.id)
                                    cartItems = getCart(token)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        enabled = cartItems.contains(product!!.id),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF5722)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                    ) {
                        Text(
                            "Remove from Cart",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { navController.navigate("catalog") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF5722)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                ) {
                    Text(
                        "Go to Catalog",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}