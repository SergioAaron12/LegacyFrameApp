package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    data class Item(val route: String, val label: String, val icon: ImageVector, val onClick: () -> Unit)
    val items = listOf(
        Item(route = "home", label = "Home", icon = Icons.Default.Home, onClick = onHome),
    Item(route = "profile", label = "Perfil", icon = Icons.Default.Person, onClick = onProfile),
    Item(route = "settings", label = "Configuraciones", icon = Icons.Default.Settings, onClick = onSettings)
    )
    val profileRoutes = setOf("profile")
    val settingsRoutes = setOf("settings", "purchases", "terms")
    NavigationBar {
        items.forEach { item ->
            val selected = when (item.route) {
                "profile" -> currentRoute in profileRoutes
                "settings" -> currentRoute in settingsRoutes
                else -> currentRoute == item.route
            }
            NavigationBarItem(
                selected = selected,
                onClick = item.onClick,
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}
