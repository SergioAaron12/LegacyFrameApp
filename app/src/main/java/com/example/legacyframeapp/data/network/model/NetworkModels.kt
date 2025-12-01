package com.example.legacyframeapp.data.network.model

import com.google.gson.annotations.SerializedName

// --- Login y Registro ---
data class LoginRequest(
    val email: String,
    @SerializedName("password") val pass: String
)
data class TokenResponse(val token: String)
data class RegisterRequest(
    val nombre: String, val apellido: String?, val email: String, val password: String,
    val confirmPassword: String, val rut: String, val dv: String, val telefono: String
)

// --- Pedidos ---
data class OrderRequest(val items: List<OrderDetail>)
data class OrderDetail(
    val productoId: Long, val nombreProducto: String, val cantidad: Int, val precioUnitario: Double
)

// --- CATÁLOGO (Esto es lo que faltaba o estaba fallando) ---
data class ProductRemote(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
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