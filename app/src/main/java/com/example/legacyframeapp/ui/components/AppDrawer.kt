package com.example.legacyframeapp.ui.components

import androidx.compose.material.icons.Icons // Íconos Material
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.Person // Ícono Registro
import androidx.compose.material.icons.filled.ListAlt // Icono para Molduras
import androidx.compose.material.icons.filled.Call // Icono para Contacto
import androidx.compose.material.icons.filled.Logout // Icono para Cerrar Sesión
import androidx.compose.material.icons.filled.Photo // Ícono para Cuadros
import androidx.compose.material.icons.filled.ShoppingCart // Ícono para Carrito
import androidx.compose.material3.Icon // Ícono en ítem del drawer
import androidx.compose.material3.NavigationDrawerItem // Ítem seleccionable
import androidx.compose.material3.NavigationDrawerItemDefaults // Defaults de estilo
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.ModalDrawerSheet // Contenedor de contenido del drawer
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.compose.ui.graphics.vector.ImageVector // Tipo de ícono

// Pequeña data class para representar cada opción del drawer
data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, // Lista de ítems a mostrar
    modifier: Modifier = Modifier // Modificador opcional
) {
    ModalDrawerSheet( // Hoja que contiene el contenido del drawer
        modifier = modifier // Modificador encadenable
    ) {
        // Recorremos las opciones y pintamos ítems
        items.forEach { item -> // Por cada ítem
            NavigationDrawerItem( // Ítem con estados Material
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier, // Sin mods extra
                colors = NavigationDrawerItemDefaults.colors() // Estilo por defecto
            )
        }
    }
}


@Composable
fun loggedOutDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onCuadros: () -> Unit,
    onMolduras: () -> Unit,
    onCart: () -> Unit,
    onContact: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),
    DrawerItem("Molduras", Icons.Filled.ListAlt, onMolduras),
    DrawerItem("Cuadros", Icons.Filled.Photo, onCuadros),
    DrawerItem("Carrito", Icons.Filled.ShoppingCart, onCart),
    DrawerItem("Contacto", Icons.Filled.Call, onContact),
    DrawerItem("Login", Icons.Filled.AccountCircle, onLogin),
    DrawerItem("Registro", Icons.Filled.Person, onRegister)
)

@Composable
fun loggedInDrawerItems(
    onHome: () -> Unit,
    onMolduras: () -> Unit, // <-- Nueva acción
    onCuadros: () -> Unit,   // <-- Nueva acción
    onCart: () -> Unit,      // <-- Nueva acción
    onContact: () -> Unit,   // <-- Nueva acción
    // (Aquí podrías añadir "onProfile" en el futuro)
    onLogout: () -> Unit   // <-- Nueva acción
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),
    DrawerItem("Molduras", Icons.Filled.ListAlt, onMolduras), // <-- NUEVO ÍTEM
    DrawerItem("Cuadros", Icons.Filled.Photo, onCuadros), // <-- NUEVO ÍTEM
    DrawerItem("Carrito", Icons.Filled.ShoppingCart, onCart), // <-- NUEVO ÍTEM
    DrawerItem("Contacto", Icons.Filled.Call, onContact), // <-- NUEVO ÍTEM
    DrawerItem("Cerrar Sesión", Icons.Filled.Logout, onLogout) // <-- NUEVO ÍTEM
)