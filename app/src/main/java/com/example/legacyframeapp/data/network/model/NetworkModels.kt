package com.example.legacyframeapp.data.network.model

import com.google.gson.annotations.SerializedName

// --- AUTH ---
data class LoginRequest(val email: String, @SerializedName("password") val pass: String)
data class TokenResponse(val token: String)
data class RegisterRequest(
    val nombre: String, val apellido: String?, val email: String,
    val password: String, val confirmPassword: String,
    val rut: String, val dv: String, val telefono: String
)

// --- PRODUCTOS (GET) ---
data class ProductRemote(
    val id: Long, val nombre: String, val descripcion: String?,
    val precio: Double, val stock: Int, val imagenUrl: String?,
    val categoria: CategoriaRemote?
)
data class CategoriaRemote(val id: Long, val nombre: String, val descripcion: String?)

// --- CREAR PRODUCTOS (POST) ---
data class CreateProductRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String,
    val categoria: CategoryIdRequest // Objeto anidado
)
data class CategoryIdRequest(val id: Long)

// --- PEDIDOS ---
data class OrderRequest(val items: List<OrderDetail>)
data class OrderDetail(val productoId: Long, val nombreProducto: String, val cantidad: Int, val precioUnitario: Double)
data class OrderResponse(val id: Long, val total: Double, val estado: String, val fechaCreacion: String?, val detalles: List<OrderDetailResponse>?)
data class OrderDetailResponse(val nombreProducto: String, val cantidad: Int, val precioUnitario: Double)

// --- CONTACTO ---
data class ContactRequest(val nombre: String, val email: String, val mensaje: String)

// --- API EXTERNA ---
data class IndicadoresResponse(val dolar: IndicadorData)
data class IndicadorData(val valor: Double, val fecha: String)