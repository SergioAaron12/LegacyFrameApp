package com.example.legacyframeapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF2E7D32), // Verde bonito
        contentColor = Color.White,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    content: @Composable () -> Unit
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled, colors = colors) {
        content()
    }
}
