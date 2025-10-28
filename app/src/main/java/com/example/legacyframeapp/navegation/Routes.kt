package com.example.legacyframeapp.navegation

// Definición centralizada de rutas de navegación para evitar strings mágicos
sealed class Route(val path: String) {
    data object Splash    : Route("splash")
    data object Home      : Route("home")
    data object Profile   : Route("profile")
    data object Login     : Route("login")
    data object Register  : Route("register")
    data object Molduras  : Route("molduras")
    data object Cuadros   : Route("cuadros")
    data object Contact   : Route("contact")
    data object Cart      : Route("cart")
    data object AddProduct: Route("add_product")
    data object AddCuadro : Route("add_cuadro")
    data object Admin     : Route("admin")
    data object ChangeProductImage : Route("change_product_image")
    data object DeleteProduct : Route("delete_product")
    data object DeleteCuadro : Route("delete_cuadro")
    data object Settings  : Route("settings")
    data object Purchases : Route("purchases")
    data object Terms     : Route("terms")
}

// Nota: usar Route evita errores al renombrar rutas y facilita el mantenimiento