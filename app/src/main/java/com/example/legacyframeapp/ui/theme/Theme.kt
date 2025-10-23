package com.example.legacyframeapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme // Solo usaremos tema claro
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // Importa Color directamente
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Paleta de Colores CLARA usando tus variables de Color.kt ---
// Esta sección toma los colores que definiste en Color.kt (PrimaryBrown, White, etc.)
// y le dice a Material Design cómo usarlos en los componentes.
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrown,         // Color principal (Usado por TopAppBar, botones llenos, etc.)
    secondary = SecondaryBrown,     // Color secundario (Menos usado por defecto)
    tertiary = AccentBrown,         // Color de acento (FloatingActionButton, badges, etc.)
    background = LightBackground,   // Fondo general de las pantallas
    surface = White,                // Fondo de elementos elevados como Cards, Menús
    onPrimary = White,              // Color del texto/iconos SOBRE el color primario (ej: texto en TopBar café)
    onSecondary = White,            // Color del texto/iconos SOBRE el color secundario
    onTertiary = TextDark,          // Color del texto/iconos SOBRE el color de acento
    onBackground = TextDark,        // Color del texto SOBRE el fondo general
    onSurface = TextDark,           // Color del texto SOBRE superficies (ej: texto en Cards blancas)
    error = ErrorRed,               // Color para mensajes de error
    onError = White                 // Color del texto SOBRE el color de error
    // Puedes definir más mapeos si los necesitas (surfaceVariant, outline, etc.)
)

// --- Función Composable del Tema ---
// Esta es la función que envuelve tu app en MainActivity.kt
@Composable
fun UINavegacionTheme( // Mantenemos el nombre del ejemplo para compatibilidad
    darkTheme: Boolean = isSystemInDarkTheme(), // No usaremos tema oscuro por ahora
    content: @Composable () -> Unit // El contenido de tu app se pasa aquí
) {
    // Forzamos el uso de la paleta clara que definimos arriba
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Efecto secundario para cambiar el color de la barra de estado del sistema (la de arriba del todo)
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Barra de estado café
            // Íconos de la barra de estado (wifi, batería) claros (false) u oscuros (true)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Íconos claros
        }
    }

    // Aplica el tema de Material 3 a todo el contenido dentro de él
    MaterialTheme(
        colorScheme = colorScheme, // Usa nuestra paleta de colores café
        typography = Typography, // Usa las fuentes definidas en Type.kt
        content = content      // Renderiza tu app (NavGraph, Screens, etc.)
    )
}