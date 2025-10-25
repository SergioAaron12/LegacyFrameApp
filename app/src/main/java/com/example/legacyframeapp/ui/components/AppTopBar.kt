package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
<<<<<<< HEAD
import androidx.compose.material.icons.filled.Menu // Solo necesitamos el ícono de Menú
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.foundation.Image
=======
import androidx.compose.material.icons.filled.Menu // Ícono de Menú
import androidx.compose.material.icons.filled.ShoppingCart // Ícono de Carrito
import androidx.compose.material3.Badge // Para el contador
import androidx.compose.material3.BadgedBox // Para poner el Badge sobre el Icono
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.example.legacyframeapp.ui.components.BrandLogoSmall
import com.example.legacyframeapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

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
<<<<<<< HEAD
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { BrandLogoSmall(tint = MaterialTheme.colorScheme.onPrimary) },
        // Botón de Menú (Hamburguesa)
=======
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
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
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
<<<<<<< HEAD
            IconButton(onClick = onOpenCart) {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito"
                        )
=======
            BadgedBox(
                badge = {
                    // Muestra el badge solo si hay items
                    if (cartItemCount > 0) {
                        Badge { Text(cartItemCount.toString()) }
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
                    }
                }
            ) {
                IconButton(onClick = onOpenCart) { // Llama a la acción correcta
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
<<<<<<< HEAD
                        contentDescription = "Carrito"
=======
                        contentDescription = "Ver Carrito"
                        // El color ya se define en 'actionIconContentColor' arriba
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
                    )
                }
            }
        }
    )
}