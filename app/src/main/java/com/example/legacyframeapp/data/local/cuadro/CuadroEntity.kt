package com.example.legacyframeapp.data.local.cuadro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuadros") 
data class CuadroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    val title: String,
    val description: String,
    val price: Int, // Precio en CLP
    val size: String, // Ej: "30x40 cm", "50x70 cm"
    val material: String, // Ej: "Canvas", "Papel fotográfico", "Acrílico"
    val category: String, // Ej: "Paisajes", "Retratos", "Abstracto", "Familiar"
    val imagePath: String, // Ruta a la imagen del cuadro
    val isCustom: Boolean = false, // true si es personalizado, false si es catálogo
    val artist: String? = null // Artista (opcional)
)