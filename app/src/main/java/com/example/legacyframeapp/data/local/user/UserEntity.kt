package com.example.legacyframeapp.data.local.user


import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity declara la tabla 'users' en SQLite local, ahora con campos de tu BD Oracle
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val rolId: Int,
    val estadoId: Int,

    // --- CAMPOS OBLIGATORIOS PARA EL USUARIO (EN EL FORMULARIO DE REGISTRO) ---
    // (Porque no tienen '?' ni un valor por defecto como '= null')
    val nombre: String,
    val rut: String,
    val dv: String,
    val phone: Int,
    val email: String,
    val password: String,
    val apellido: String?,          //  OPCIONAL.
    val calle: String? = null,      // OPCIONAL.
    val numero: String? = null,     //  OPCIONAL.
    val departamento: String? = null, //  OPCIONAL.
    val comunaId: Int? = null       //  OPCIONAL.
)
