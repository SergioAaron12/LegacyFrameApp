package com.example.legacyframeapp.data.network.model

import com.google.gson.annotations.SerializedName

// ==================================================================
// 1. MICROSERVICIO DE AUTENTICACIÓN (Puerto 8085)
// ==================================================================

data class LoginRequest(
    val email: String,
    // El backend espera "password", pero si tu UI usa "pass", esto lo traduce automáticamente
    @SerializedName("password") val pass: String
)

data class TokenResponse(
    val token: String
)

data class RegisterRequest(
    val nombre: String,
    val apellido: String?,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val rut: String,
    val dv: String,
    val telefono: String
)

// ==================================================================
// 2. MICROSERVICIO DE PRODUCTOS (Puerto 8083)
// ==================================================================

// Este modelo coincide con el JSON que envía tu backend Spring Boot
data class ProductRemote(
    val id: Long,
    val nombre: String,
    val descripcion: String?, // Puede venir nulo
    val precio: Double,
    val stock: Int,
    val imagenUrl: String?,
    val categoria: CategoriaRemote?
)

data class CategoriaRemote(
    val id: Long,
    val nombre: String,
    val descripcion: String?
)

// ==================================================================
// 3. MICROSERVICIO DE PEDIDOS (Puerto 8084)
// ==================================================================

data class OrderRequest(
    val items: List<OrderDetail>
)

data class OrderDetail(
    val productoId: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
)

// ==================================================================
// 4. MICROSERVICIO DE CONTACTO (Puerto 8081)
// ==================================================================

data class ContactRequest(
    val nombre: String,
    val email: String,
    val mensaje: String
)