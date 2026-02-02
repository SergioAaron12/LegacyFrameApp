package com.example.legacyframeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el tema claro (Light Theme)
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

// Paleta de colores para el tema oscuro (Dark Theme)
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

/**
 * Función principal del tema de la aplicación.
 * Permite configurar el tema oscuro/claro, color dinámico (Android 12+),
 * un color de acento personalizado y escalado de fuentes.
 */
@Composable
fun UINavegacionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Habilita colores dinámicos del sistema en Android 12+
    accentHex: String? = null,    // Opcional: Permite sobreescribir el color primario con un valor hexadecimal
    fontScale: Float = 1f,        // Opcional: Permite escalar el tamaño de todas las fuentes
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Determina el esquema de color base
    var colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Sobrescribe el color primario si se proporciona un accentHex válido
    accentHex?.let { hex ->
        try {
            val accentColor = Color(android.graphics.Color.parseColor(hex))
            colorScheme = colorScheme.copy(primary = accentColor)
        } catch (e: IllegalArgumentException) {
            // El color hexadecimal no es válido, no hacemos nada
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        // Efecto secundario para cambiar el color de las barras de sistema (estado y navegación)
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            // Configura el color de los iconos de las barras de sistema (claros u oscuros)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    // Escala la tipografía según el factor 'fontScale'
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

    // Aplica el tema de Material 3 al contenido de la app
    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}
