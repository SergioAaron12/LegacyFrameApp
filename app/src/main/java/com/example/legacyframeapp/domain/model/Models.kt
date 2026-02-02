package com.example.legacyframeapp.domain.model

// Modelo para Productos (Molduras) que usa la UI
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val category: String
)

// Modelo para Cuadros que usa la UI
data class Cuadro(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val category: String = "Cuadros",
    // Campos opcionales para compatibilidad con tu UI actual
    val size: String = "",
    val material: String = "",
    val artist: String = ""
)

// Modelo para el Historial de Compras (Reemplaza a OrderEntity)
data class Order(
    val id: Long = 0,
    val dateMillis: Long,
    val itemsText: String, // Resumen de items (ej: "Marco x2...")
    val total: Int
)

// Modelo de Usuario (Reemplaza a UserEntity para la sesión en memoria)
data class User(
    val id: String = "0",
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val phone: String? = null
)