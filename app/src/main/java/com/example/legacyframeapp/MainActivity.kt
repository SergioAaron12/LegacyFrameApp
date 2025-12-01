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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.legacyframeapp.data.local.database.AppDatabase
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.repository.ContactRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.OrderRepository
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.navegation.AppNavGraph
import com.example.legacyframeapp.ui.theme.UINavegacionTheme
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear el canal de notificaciones para las compras
        createNotificationChannel(this)

        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // 1. Base de Datos Local (Solo para el Carrito)
    val db = AppDatabase.getDatabase(context)
    val cartDao = db.cartDao()

    // 2. Preferencias de Usuario (Token, Tema, etc.)
    val userPrefs = UserPreferences(context)

    // 3. Crear Repositorios
    // UserRepository ahora usa Preferences para guardar el token y Retrofit internamente
    val userRepository = UserRepository(userPrefs)

    // ProductRepository y CuadroRepository ahora usan Retrofit (sin argumentos de constructor)
    val productRepository = ProductRepository()
    val cuadroRepository = CuadroRepository()

    // CartRepository sigue usando el DAO local (Room)
    val cartRepository = CartRepository(cartDao)

    // OrderRepository usa Retrofit (sin argumentos)
    val orderRepository = OrderRepository()

    // ContactRepository usa Retrofit (sin argumentos)
    val contactRepository = ContactRepository()

    // 4. Crear el ViewModel usando la Factory
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            application = application,
            userRepository = userRepository,
            productRepository = productRepository,
            cuadroRepository = cuadroRepository,
            cartRepository = cartRepository,
            userPreferences = userPrefs,
            orderRepository = orderRepository,
            contactRepository = contactRepository
        )
    )

    // 5. Configurar Navegación y Tema
    val navController = rememberNavController()

    // Observar estados de preferencias para el tema dinámico
    val darkMode by authViewModel.darkMode.collectAsStateWithLifecycle()
    val themeMode by authViewModel.themeMode.collectAsStateWithLifecycle()
    val accentHex by authViewModel.accentColor.collectAsStateWithLifecycle()
    val fontScale by authViewModel.fontScale.collectAsStateWithLifecycle()

    // Resolver si usar tema oscuro o claro según configuración
    val resolvedDark = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> darkMode // "system" usa la configuración del sistema (darkMode)
    }

    UINavegacionTheme(
        darkTheme = resolvedDark,
        accentHex = accentHex,
        fontScale = fontScale
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Efecto secundario: Precargar imágenes al iniciar (opcional)
            LaunchedEffect(Unit) {
                authViewModel.prefetchProductImages(context.applicationContext)
            }

            // Iniciar el Grafo de Navegación
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}

// Función auxiliar para crear el canal de notificaciones (Android 8.0+)
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