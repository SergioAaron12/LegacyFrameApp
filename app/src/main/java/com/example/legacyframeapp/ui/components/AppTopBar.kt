package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu // Solo necesitamos el ícono de Menú
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onGoCart: () -> Unit,
    cartItemCount: Int
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
        // --- AÑADIR 'actions' (Iconos a la derecha) ---
        actions = {
            // Caja que permite poner un "Badge" (globo) sobre un ícono
            BadgedBox(
                badge = {
                    // Solo muestra el Badge si hay items
                    if (cartItemCount > 0) {
                        Badge {
                            Text(cartItemCount.toString())
                        }
                    }
                }
            ) {
                // El ícono del carrito en sí
                IconButton(onClick = onGoCart) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Ver Carrito"
                    )
                }
            }
        },

        // Botón de Menú (Hamburguesa)
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Abrir menú de navegación"
                )
            }
        }
        // Nota: Ya no hay 'actions' (los 3 puntitos, etc.)
    )
}