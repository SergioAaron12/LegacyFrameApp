package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu // Solo necesitamos el ícono de Menú
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
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
    onOpenDrawer: () -> Unit, // La única acción que necesita
    cartCount: Int,
    onOpenCart: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Tu color café
            titleContentColor = MaterialTheme.colorScheme.onPrimary, // Blanco
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary // Blanco
        ),
        title = {
            Text(
                "Legacy Frames", // Título de tu App
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        // Botón de Menú (Hamburguesa)
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Abrir menú de navegación"
                )
            }
        },
        actions = {
            IconButton(onClick = onOpenCart) {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Carrito",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}