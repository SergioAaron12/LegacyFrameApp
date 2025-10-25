package com.example.legacyframeapp.navegation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
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
    data object Settings  : Route("settings")
    data object Purchases : Route("purchases")
    data object Terms     : Route("terms")
}

/*
* “Strings mágicos” se refiere a cuando pones un texto duro y repetido en varias partes del código,
* Si mañana cambias "home" por "inicio", tendrías que buscar todas las ocurrencias de "home" a mano.
* Eso es frágil y propenso a errores.
La idea es: mejor centralizar esos strings en una sola clase (Route), y usarlos desde ahí.*/