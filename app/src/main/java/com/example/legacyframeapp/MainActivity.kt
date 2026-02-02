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
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.legacyframeapp.navegation.AppNavGraph
import com.example.legacyframeapp.ui.theme.UINavegacionTheme
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear el canal de notificaciones para las compras
        createNotificationChannel(this)

        val appContainer = (application as LegacyFrameApplication).container

        setContent {
            AppRoot(appContainer.authViewModelFactory)
        }
    }
}

@Composable
fun AppRoot(authViewModelFactory: com.example.legacyframeapp.ui.viewmodel.AuthViewModelFactory) {
    // 4. Crear el ViewModel usando la Factory
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

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

            // --- AQUÍ QUITAMOS LA LLAMADA QUE DABA ERROR ---
            // Ya no es necesario llamar a prefetchProductImages

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
