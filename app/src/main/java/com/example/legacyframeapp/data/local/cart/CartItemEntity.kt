package com.example.legacyframeapp.data.local.cart

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.legacyframeapp.data.local.product.ProductEntity

@Entity(
    tableName = "cart_items",
    // --- Índices y Claves Foráneas ---
    // Esto asegura que un 'productId' solo pueda estar una vez en el carrito
    indices = [Index(value = ["productId"], unique = true)],

    // (Opcional pero recomendado) Esto conecta la tabla 'cart_items' con 'products'.
    // Si un producto se borra, la fila del carrito también se borra (onDelete = CASCADE).
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // --- Datos del Producto ---
    val productId: Long, // El ID del ProductEntity
    val quantity: Int,   // La cantidad que el usuario añadió

    // --- Datos "Denormalizados" ---
    // Copiamos estos datos del producto aquí para no tener que
    // hacer "joins" de base de datos. Facilita mucho mostrar el carrito.
    val productName: String,
    val productPrice: Int,
    val productImagePath: String // La ruta a la imagen guardada
)