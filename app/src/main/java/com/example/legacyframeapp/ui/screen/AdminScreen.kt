package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AdminScreen(
    onGoAddProduct: () -> Unit,
    onGoAddCuadro: () -> Unit,
    onGoDeleteProduct: () -> Unit,
    onGoDeleteCuadro: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Panel de Administrador", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Botones Grandes
        AdminButton("Agregar Moldura", Icons.Default.AddBox, onGoAddProduct)
        Spacer(modifier = Modifier.height(16.dp))
        AdminButton("Agregar Cuadro", Icons.Default.AddPhotoAlternate, onGoAddCuadro)
        Spacer(modifier = Modifier.height(16.dp))
        AdminButton("Eliminar Moldura", Icons.Default.Delete, onGoDeleteProduct)
        Spacer(modifier = Modifier.height(16.dp))
        AdminButton("Eliminar Cuadro", Icons.Default.DeleteForever, onGoDeleteCuadro)
    }
}

@Composable
fun AdminButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}