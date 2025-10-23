package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.user.UserDao
import com.example.legacyframeapp.data.local.user.UserEntity

// Constantes para valores por defecto (asegúrate que coincidan con tu BD Oracle)
private const val CLIENTE_ROL_ID = 2
private const val ACTIVO_ESTADO_ID = 1

class UserRepository(
    private val userDao: UserDao
) {

    // Login (sin cambios por ahora)
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email.trim().lowercase())
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // --- FUNCIÓN REGISTER MODIFICADA ---
    suspend fun register(
        nombre: String,
        apellido: String?, // Recibe apellido (nullable)
        rut: String,       // Recibe rut
        dv: String,        // Recibe dv
        email: String,
        password: String,
        phone: Int,
        calle: String? = null,
        numero: String? = null,
        departamento: String? = null,
        comunaId: Int? = null
    ): Result<Long> { // Devuelve el ID del usuario insertado o un error

        // 1. Validar si el email ya existe (sin cambios)
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }

        // 2. Crear la entidad UserEntity usando los nuevos parámetros
        val newUser = UserEntity(
            // id se genera automáticamente
            nombre = nombre.trim(),
            apellido = apellido?.trim(), // Usa apellido si viene, si no, será null
            rut = rut.trim(),            // Limpia espacios del rut
            dv = dv.trim().uppercase(),  // Limpia y guarda DV en mayúscula
            email = email.trim().lowercase(), // Limpia y guarda email en minúscula
            phone = phone,
            password = password,          // Recordatorio: Usar hash en producción
            rolId = CLIENTE_ROL_ID,       // Asigna rol cliente por defecto
            estadoId = ACTIVO_ESTADO_ID,   // Asigna estado activo por defecto
            // Campos de dirección (se guardan si vienen, si no, quedan null)
            calle = calle?.trim(),
            numero = numero?.trim(),
            departamento = departamento?.trim(),
            comunaId = comunaId
        )

        // 3. Intentar insertar en la base de datos
        return try {
            val id = userDao.insert(newUser)
            Result.success(id) // Devuelve el ID si la inserción fue exitosa
        } catch (e: Exception) {
            // Captura cualquier error durante la inserción
            Result.failure(e)
        }
    }

    // --- Otras funciones podrían ir aquí (ej: getUserById, updateProfile, etc.) ---
}