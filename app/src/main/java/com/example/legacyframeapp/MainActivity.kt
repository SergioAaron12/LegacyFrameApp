package com.example.legacyframeapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.legacyframeapp.ui.theme.UINavegacionTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.data.local.database.AppDatabase
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.navegation.AppNavGraph
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.ui.viewmodel.AuthViewModelFactory
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.repository.OrderRepository


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- para el canal de notificación ---
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

    val userPrefs = UserPreferences(context)

    // Repositorio de órdenes (historial de compras)
    val orderDao = try { db.orderDao() } catch (e: Exception) { null }
    val orderRepository = orderDao?.let { OrderRepository(it) }

    // Crea el ViewModel usando la Factory
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            application = application,
            userRepository = userRepository,
            productRepository = productRepository,
            cuadroRepository = cuadroRepository,
            cartRepository = cartRepository,
            userPreferences = userPrefs,
            orderRepository = orderRepository
        )
    )

    val navController = rememberNavController()

    val darkMode by authViewModel.darkMode.collectAsStateWithLifecycle()
    val themeMode by authViewModel.themeMode.collectAsStateWithLifecycle()
    val accentHex by authViewModel.accentColor.collectAsStateWithLifecycle()
    val fontScale by authViewModel.fontScale.collectAsStateWithLifecycle()
    val resolvedDark = when(themeMode){
        "light" -> false
        "dark" -> true
        else -> darkMode // system fallback: usar flujo existente
    }
    UINavegacionTheme(darkTheme = resolvedDark, accentHex = accentHex, fontScale = fontScale) {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Prefetch de imágenes
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