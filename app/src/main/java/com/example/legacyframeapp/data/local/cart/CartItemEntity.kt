package com.example.legacyframeapp.data.local.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Identificador del origen del item
    val type: String,   // "product" | "cuadro"
    val refId: Long,    // ID del ProductEntity o CuadroEntity

    // Snapshot de datos para mostrar
    val name: String,
    val price: Int,
    val imagePath: String? = null,

    val quantity: Int = 1
)
