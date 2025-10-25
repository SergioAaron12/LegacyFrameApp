package com.example.legacyframeapp

import android.app.Application // <-- 1. AÑADIR IMPORT
import android.app.NotificationChannel // <-- Para Notificaciones
import android.app.NotificationManager // <-- Para Notificaciones
import android.content.Context // <-- Para Notificaciones
import android.os.Build // <-- Para Notificaciones
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.legacyframeapp.ui.theme.UINavegacionTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import com.example.legacyframeapp.data.local.database.AppDatabase
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.navegation.AppNavGraph
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.ui.viewmodel.AuthViewModelFactory
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.CartRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- 2. AÑADIR ESTA LÍNEA (para el canal de notificación) ---
        createNotificationChannel(this)

        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current

    // --- 3. OBTENER APPLICATION ---
    val application = context.applicationContext as Application

    val db = AppDatabase.getDatabase(context)

    // DAOs y Repos
    val userDao = db.userDao()
    val userRepository = UserRepository(userDao)
    val productDao = db.productDao()
    val productRepository = ProductRepository(productDao)
    val cuadroDao = db.cuadroDao()
    val cuadroRepository = CuadroRepository(cuadroDao)
    val cartDao = db.cartDao()
    val cartRepository = CartRepository(cartDao)

    // --- 4. MODIFICAR LA CREACIÓN DE LA FACTORY ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            application = application, // <-- PASAR APPLICATION PRIMERO
            userRepository = userRepository,
            productRepository = productRepository,
            cuadroRepository = cuadroRepository,
            cartRepository = cartRepository
        )
    )

    val navController = rememberNavController()

    UINavegacionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Prefetch de imágenes (lógica de tu compañero)
            LaunchedEffect(Unit) {
                authViewModel.prefetchProductImages(context.applicationContext)
            }
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}

// --- 5. AÑADIR ESTA FUNCIÓN AL FINAL DEL ARCHIVO ---
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "purchase_notifications"
        val name = "Notificaciones de Compra"
        val descriptionText = "Canal para notificar compras exitosas."
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}