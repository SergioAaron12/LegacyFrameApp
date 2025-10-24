package com.example.legacyframeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// Importa TU tema personalizado
import com.example.legacyframeapp.ui.theme.UINavegacionTheme
import androidx.compose.material3.Surface // Surface sigue siendo necesario
import androidx.compose.material3.MaterialTheme // Necesario para acceder a colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel // Para obtener el ViewModel
import androidx.navigation.compose.rememberNavController
// Importaciones de tu capa de datos y navegación
import com.example.legacyframeapp.data.local.database.AppDatabase
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.navegation.AppNavGraph
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.ui.viewmodel.AuthViewModelFactory
import com.example.legacyframeapp.data.repository.ProductRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita pantalla completa (edge-to-edge)
        setContent {
            AppRoot() // Llama a la función Composable raíz
        }
    }
}

// Composable raíz que configura el ViewModel y la Navegación
@Composable
fun AppRoot() {
    // --- Inicialización del ViewModel ---
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    // DAO de Usuario (existente)
    val userDao = db.userDao()
    val userRepository = UserRepository(userDao)

    // --- AÑADIR ESTO ---
    // DAO de Producto (nuevo)
    val productDao = db.productDao()
    // Repositorio de Producto (nuevo)
    val productRepository = ProductRepository(productDao)
    // ---------------------

    // Crea el ViewModel usando la Factory (AHORA PASAMOS AMBOS REPOS)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            userRepository = userRepository,
            productRepository = productRepository // <--- AÑADIR ESTO
        )
    )
    // ------------------------------------

    val navController = rememberNavController()

    // ... (El resto de tu UINavegacionTheme sigue igual) ...
    UINavegacionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}