package com.example.legacyframeapp
import android.app.Application
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
import com.example.legacyframeapp.data.repository.CartRepository
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
    // --- Inicialización ---
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = AppDatabase.getDatabase(context)

    // --- Repositorios existentes ---
    val userDao = db.userDao()
    val userRepository = UserRepository(userDao)

    val productDao = db.productDao()
    val productRepository = ProductRepository(productDao)

    // --- AÑADIR ESTO ---
    val cartDao = db.cartDao() // 1. Obtener el DAO del carrito
    val cartRepository = CartRepository(cartDao) // 2. Crear el Repo del carrito
    // ---------------------

    // --- Modificar la Factory ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            application = application,
            userRepository = userRepository,
            productRepository = productRepository,
            cartRepository = cartRepository // <--- 3. AÑADIR ESTO
        )
    )
    // ------------------------------------

    val navController = rememberNavController()

    UINavegacionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}