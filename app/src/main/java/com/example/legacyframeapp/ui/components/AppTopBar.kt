package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu // Solo necesitamos el ícono de Menú
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.foundation.Image
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
import com.example.legacyframeapp.ui.components.BrandLogoSmall
import com.example.legacyframeapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit, // La única acción que necesita
    cartCount: Int,
    onOpenCart: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { BrandLogoSmall(tint = MaterialTheme.colorScheme.onPrimary) },
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
                            contentDescription = "Carrito"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Carrito"
                    )
                }
            }
        }
    )
}