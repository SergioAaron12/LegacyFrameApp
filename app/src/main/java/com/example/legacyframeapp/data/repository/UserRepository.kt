package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.user.UserDao
import com.example.legacyframeapp.data.local.user.UserEntity

// Constantes para valores por defecto (asegúrate que coincidan con tu BD Oracle)
private const val ADMIN_ROL_ID = 1
private const val CLIENTE_ROL_ID = 2
private const val ACTIVO_ESTADO_ID = 1

class UserRepository(
    private val userDao: UserDao
) {

    // Login (con soporte explícito para credenciales de administrador)
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val normalizedEmail = email.trim().lowercase()

        // Regla explícita: Admin@legacyframes.cl con contraseña Admin123! es administrador
        if (normalizedEmail == "admin@legacyframes.cl" && password == "Admin123!") {
            // Asegura que exista el usuario admin en BD; si no, créalo
            val existing = userDao.getByEmail(normalizedEmail)
            if (existing != null) {
                // Si existe, devuélvelo tal cual (AuthViewModel usará rolId==1 para isAdmin)
                return Result.success(existing)
            } else {
                // Crear y devolver el usuario admin
                val admin = UserEntity(
                    nombre = "Admin",
                    apellido = "Legacy",
                    phone = 12345678,
                    rut = "11111111",
                    dv = "1",
                    email = normalizedEmail,
                    password = password,
                    rolId = ADMIN_ROL_ID,
                    estadoId = ACTIVO_ESTADO_ID
                )
                return try {
                    userDao.insert(admin)
                    val inserted = userDao.getByEmail(normalizedEmail)
                    if (inserted != null) Result.success(inserted)
                    else Result.failure(IllegalStateException("No se pudo crear el administrador"))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }

        // Login normal
        val user = userDao.getByEmail(normalizedEmail)
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