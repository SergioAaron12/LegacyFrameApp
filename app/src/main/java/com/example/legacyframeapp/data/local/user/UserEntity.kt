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
    val nombre: String,
    val rut: String,
    val dv: String,
    val phone: Int,
    val email: String,
    val password: String,
    val apellido: String?,
    val calle: String? = null,
    val numero: String? = null,
    val departamento: String? = null,
    val comunaId: Int? = null
)
