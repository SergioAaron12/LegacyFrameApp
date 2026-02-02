package com.example.legacyframeapp.data.local.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,      // "product" o "cuadro"
    val refId: Long,       // ID original del producto
    val name: String,
    val price: Int,
    val imageUrl: String,  // <--- ESTE ES EL NOMBRE CORRECTO QUE NECESITAMOS
    val quantity: Int = 1
)