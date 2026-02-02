package com.example.legacyframeapp.data.network.model

import com.google.gson.annotations.SerializedName

// ==================================================================
// 1. AUTH (LOGIN Y REGISTRO)
// ==================================================================
data class LoginRequest(
    val email: String,
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
// 2. PERFIL DE USUARIO (¡NUEVO!)
// ==================================================================
// Lo que recibimos del servidor al pedir los datos (/auth/perfil)
data class UserProfileResponse(
    val nombre: String,
    val apellido: String?,
    val email: String,
    val telefono: String?,
    val direccion: String?
)

// Lo que enviamos al servidor para actualizar (/auth/profile)
data class UpdateProfileRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val direccion: String,
    val password: String? = null,        // Opcional (si viene null, no se cambia)
    val confirmPassword: String? = null  // Opcional
)

// ==================================================================
// 3. PRODUCTOS (CATÁLOGO)
// ==================================================================
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

// Para Crear Productos (POST)
data class CreateProductRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String,
    val categoria: CategoryIdRequest // Objeto anidado con el ID
)

data class CategoryIdRequest(
    val id: Long
)

// ==================================================================
// 4. PEDIDOS
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

data class OrderResponse(
    val id: Long,
    val total: Double,
    val estado: String,
    val fechaCreacion: String?,
    val detalles: List<OrderDetailResponse>?
)

data class OrderDetailResponse(
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
)

// ==================================================================
// 5. CONTACTO
// ==================================================================
data class ContactRequest(
    val nombre: String,
    val email: String,
    val mensaje: String
)

// ==================================================================
// 6. API EXTERNA (INDICADORES ECONÓMICOS)
// ==================================================================
data class IndicadoresResponse(
    val dolar: IndicadorData
)

data class IndicadorData(
    val valor: Double,
    val fecha: String
)