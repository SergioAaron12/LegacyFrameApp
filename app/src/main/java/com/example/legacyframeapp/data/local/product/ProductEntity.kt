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

    // Esta es la columna CLAVE que discutimos.
    // Aquí guardaremos la ruta al archivo de imagen en el teléfono.
    // Ej: "moldura_1701393.jpg"
    val imagePath: String
)