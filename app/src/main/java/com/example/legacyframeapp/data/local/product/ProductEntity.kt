package com.example.legacyframeapp.data.local.product
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products") // Nombre de la tabla
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val description: String,
    val price: Int, // Usamos Int para CLP (ej: 25000)

    val category: String = "otros", // grecas, rusticas, naturales, nativas, finger-joint

    // Aquí guardaremos la ruta o nombre del recurso para la imagen
    val imagePath: String
)