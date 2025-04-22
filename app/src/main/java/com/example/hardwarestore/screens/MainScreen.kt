package com.example.hardwarestore.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.hardwarestore.repository.LocalStorage
import com.example.hardwarestore.repository.authUser
import com.example.hardwarestore.repository.getCatalog
import com.example.hardwarestore.navigation.Navigation
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopApp() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isTokenChecked by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val fetchedCatalog = getCatalog()
        LocalStorage.saveCatalog(context, fetchedCatalog)
        if (!isTokenChecked) {
            LocalStorage.getUserData(context).collect { user ->
                val token = user.first
                if (token != null) {
                    val success = authUser(token)
                    if (!success) {
                        scope.launch {
                            LocalStorage.clearUserData(context)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hardware Store",
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
    ) { paddingValues ->
        Navigation(navController, paddingValues)
    }
}