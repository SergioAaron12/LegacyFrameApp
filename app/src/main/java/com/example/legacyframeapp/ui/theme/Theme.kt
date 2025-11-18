package com.example.legacyframeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // Importa Color directamente
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Paleta de Colores CLARA usando tus variables de Color.kt ---
// Esta sección toma los colores que definiste en Color.kt (PrimaryBrown, White, etc.)
// y le dice a Material Design cómo usarlos en los componentes.
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrown,
    onPrimary = White,
    primaryContainer = PrimaryContainerBrown,
    onPrimaryContainer = OnPrimaryContainerBrown,
    secondary = SecondaryBrown,
    onSecondary = White,
    secondaryContainer = SecondaryContainerBrown,
    onSecondaryContainer = OnSecondaryContainerBrown,
    tertiary = AccentBrown,
    onTertiary = TextDark,
    tertiaryContainer = AccentContainerBrown,
    onTertiaryContainer = OnAccentContainerBrown,
    background = LightBackground, // crema claro
    onBackground = TextDark,
    surface = LightBackground, // unifica superficie a crema café claro
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF2E7DB), // variante ligeramente más oscura
    outline = DarkBrown,
    error = ErrorRed,
    onError = White,
    inverseSurface = DarkBrown,
    inverseOnSurface = White
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBrown,
    onPrimary = White,
    primaryContainer = PrimaryContainerBrown,
    onPrimaryContainer = OnPrimaryContainerBrown,
    secondary = SecondaryBrown,
    onSecondary = White,
    secondaryContainer = SecondaryContainerBrown,
    onSecondaryContainer = OnSecondaryContainerBrown,
    tertiary = AccentBrown,
    onTertiary = White,
    tertiaryContainer = AccentContainerBrown,
    onTertiaryContainer = OnAccentContainerBrown,
    background = Color(0xFF1A140E), // fondo marrón muy oscuro (en vez de negro puro)
    onBackground = White,
    surface = Color(0xFF241A12), // superficie ligeramente más clara que background
    onSurface = White,
    surfaceVariant = Color(0xFF2E2219), // variante para list items
    outline = DarkOutline,
    error = ErrorRed,
    onError = White,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface
)

// --- Función Composable del Tema ---
// Esta es la función que envuelve tu app en MainActivity.kt
@Composable
fun UINavegacionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    accentHex: String? = null,
    fontScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Accent override (solo primary)
    val accentColor: Color? = accentHex?.let {
        try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { null }
    }
    if (accentColor != null) {
        baseScheme = baseScheme.copy(primary = accentColor)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Efecto secundario para cambiar el color de la barra de estado del sistema (la de arriba del todo)
        SideEffect {
            val window = (view.context as Activity).window
            // Colores de system bars coherentes con el fondo
            // Ajuste: usar primary para status bar para pedido de color café superior
            window.statusBarColor = baseScheme.primary.toArgb()
            window.navigationBarColor = baseScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    // Aplica el tema de Material 3 a todo el contenido dentro de él
    // Escalado tipográfico simple
    val scaledTypography = Typography.copy(
        displayLarge = Typography.displayLarge.copy(fontSize = Typography.displayLarge.fontSize * fontScale),
        displayMedium = Typography.displayMedium.copy(fontSize = Typography.displayMedium.fontSize * fontScale),
        displaySmall = Typography.displaySmall.copy(fontSize = Typography.displaySmall.fontSize * fontScale),
        headlineLarge = Typography.headlineLarge.copy(fontSize = Typography.headlineLarge.fontSize * fontScale),
        headlineMedium = Typography.headlineMedium.copy(fontSize = Typography.headlineMedium.fontSize * fontScale),
        headlineSmall = Typography.headlineSmall.copy(fontSize = Typography.headlineSmall.fontSize * fontScale),
        titleLarge = Typography.titleLarge.copy(fontSize = Typography.titleLarge.fontSize * fontScale),
        titleMedium = Typography.titleMedium.copy(fontSize = Typography.titleMedium.fontSize * fontScale),
        titleSmall = Typography.titleSmall.copy(fontSize = Typography.titleSmall.fontSize * fontScale),
        bodyLarge = Typography.bodyLarge.copy(fontSize = Typography.bodyLarge.fontSize * fontScale),
        bodyMedium = Typography.bodyMedium.copy(fontSize = Typography.bodyMedium.fontSize * fontScale),
        bodySmall = Typography.bodySmall.copy(fontSize = Typography.bodySmall.fontSize * fontScale),
        labelLarge = Typography.labelLarge.copy(fontSize = Typography.labelLarge.fontSize * fontScale),
        labelMedium = Typography.labelMedium.copy(fontSize = Typography.labelMedium.fontSize * fontScale),
        labelSmall = Typography.labelSmall.copy(fontSize = Typography.labelSmall.fontSize * fontScale)
    )
    MaterialTheme(colorScheme = baseScheme, typography = scaledTypography, content = content)
}