package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onOpenCart: () -> Unit,
    cartItemCount: Int
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { BrandLogoSmall(tint = MaterialTheme.colorScheme.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Abrir menú"
                )
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (cartItemCount > 0) {
                        Badge { Text(cartItemCount.toString()) }
                    }
                }
            ) {
                IconButton(onClick = onOpenCart) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Carrito"
                    )
                }
            }
        }
    )
}