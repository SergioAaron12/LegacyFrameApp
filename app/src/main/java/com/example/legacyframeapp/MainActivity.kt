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
    val context = LocalContext.current // Obtiene el contexto actual
    val db = AppDatabase.getDatabase(context) // Obtiene instancia de la base de datos
    val userDao = db.userDao() // Obtiene el DAO de usuario
    val userRepository = UserRepository(userDao) // Crea el repositorio con el DAO
    // Crea el ViewModel usando la Factory para inyectar el repositorio
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )
    // ------------------------------------

    val navController = rememberNavController() // Controlador de navegación

    // --- APLICA TU TEMA PERSONALIZADO ---
    // Reemplaza MaterialTheme por el nombre de tu tema (UINavegacionTheme)
    UINavegacionTheme {
        // Surface actúa como el contenedor base con el color de fondo del tema
        Surface(color = MaterialTheme.colorScheme.background) {
            // Carga el grafo de navegación, pasando el ViewModel
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel // Pasa la instancia única del ViewModel
            )
        }
    }
    // ------------------------------------
}