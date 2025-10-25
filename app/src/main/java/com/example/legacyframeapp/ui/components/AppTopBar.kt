package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu // Ícono de Menú
import androidx.compose.material.icons.filled.ShoppingCart // Ícono de Carrito
import androidx.compose.material3.Badge // Para el contador
import androidx.compose.material3.BadgedBox // Para poner el Badge sobre el Icono
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    // --- PARÁMETROS CORREGIDOS ---
    onOpenCart: () -> Unit,   // <-- Nombre correcto
    cartItemCount: Int      // <-- Nombre correcto
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Café
            titleContentColor = MaterialTheme.colorScheme.onPrimary, // Blanco
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary, // Blanco
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Blanco para el carrito
        ),
        title = {
            Text(
                "Legacy Frames",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Abrir menú"
                )
            }
        },
        // --- LÓGICA DEL CARRITO ---
        actions = {
            BadgedBox(
                badge = {
                    // Muestra el badge solo si hay items
                    if (cartItemCount > 0) {
                        Badge { Text(cartItemCount.toString()) }
                    }
                }
            ) {
                IconButton(onClick = onOpenCart) { // Llama a la acción correcta
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Ver Carrito"
                        // El color ya se define en 'actionIconContentColor' arriba
                    )
                }
            }
        }
    )
}