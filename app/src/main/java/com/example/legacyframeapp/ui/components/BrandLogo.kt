package com.example.legacyframeapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Logo vectorial simple basado en el adjunto: un recuadro con "L" y el texto "LegacyFrame".
 * Se puede tintar y escalar para usar en AppBar (small) o en secciones grandes (large).
 */
@Composable
fun BrandLogo(
    tint: Color,
    boxSize: Dp,
    strokeWidth: Dp,
    textSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Caja cuadrada con borde y una "L" centrada
        Box(
            modifier = Modifier
                .size(boxSize)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color.Transparent,
                contentColor = tint,
                border = BorderStroke(strokeWidth, tint),
                modifier = Modifier.matchParentSize(),
                shape = RoundedCornerShape(2.dp)
            ) {}
            Text(
                text = "L",
                color = tint,
                fontWeight = FontWeight.Bold,
                fontSize = textSize
            )
        }

        Spacer(Modifier.width(8.dp))
        Text(
            text = "LegacyFrame",
            color = tint,
            fontWeight = FontWeight.Medium,
            fontSize = textSize
        )
    }
}

@Composable
fun BrandLogoSmall(
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    BrandLogo(
        tint = tint,
        boxSize = 22.dp,
        strokeWidth = 1.5.dp,
        textSize = MaterialTheme.typography.titleSmall.fontSize,
        modifier = modifier
    )
}

@Composable
fun BrandLogoLarge(
    tint: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    BrandLogo(
        tint = tint,
        boxSize = 40.dp,
        strokeWidth = 2.dp,
        textSize = MaterialTheme.typography.headlineSmall.fontSize,
        modifier = modifier
    )
}
