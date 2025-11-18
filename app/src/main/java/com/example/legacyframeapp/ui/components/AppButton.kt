package com.example.legacyframeapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppButtonVariant = AppButtonVariant.Filled,
    colorsOverride: ButtonColors? = null,
    content: @Composable () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val colors: ButtonColors = when (variant) {
        AppButtonVariant.Filled -> ButtonDefaults.buttonColors(
            containerColor = scheme.primary,
            contentColor = scheme.onPrimary,
            disabledContainerColor = scheme.primary.copy(alpha = 0.40f),
            disabledContentColor = scheme.onPrimary.copy(alpha = 0.55f)
        )
        AppButtonVariant.Tonal -> ButtonDefaults.buttonColors(
            containerColor = scheme.primaryContainer,
            contentColor = scheme.onPrimaryContainer,
            disabledContainerColor = scheme.primaryContainer.copy(alpha = 0.45f),
            disabledContentColor = scheme.onPrimaryContainer.copy(alpha = 0.55f)
        )
        AppButtonVariant.Outlined -> ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = scheme.primary,
            disabledContentColor = scheme.primary.copy(alpha = 0.40f)
        )
    }
    val colorsToUse = colorsOverride ?: colors

    when (variant) {
        AppButtonVariant.Filled, AppButtonVariant.Tonal -> {
            Button(onClick = onClick, modifier = modifier, enabled = enabled, colors = colorsToUse) { content() }
        }
        AppButtonVariant.Outlined -> {
            androidx.compose.material3.OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled, colors = colorsToUse) { content() }
        }
    }
}

enum class AppButtonVariant { Filled, Tonal, Outlined }
