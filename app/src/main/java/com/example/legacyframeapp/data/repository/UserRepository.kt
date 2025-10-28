package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.user.UserDao
import com.example.legacyframeapp.data.local.user.UserEntity

// Constantes para valores por defecto (asegúrate que coincidan con tu BD Oracle)
private const val ADMIN_ROL_ID = 1 // Rol Admin
private const val CLIENTE_ROL_ID = 2
private const val ACTIVO_ESTADO_ID = 1

class UserRepository(
    private val userDao: UserDao
) {

    // --- FUNCIÓN LOGIN (ACTUALIZADA POR TU COMPAÑERO) ---
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val normalizedEmail = email.trim().lowercase()

        // Regla explícita: Admin@legacyframes.cl con contraseña Admin123! es administrador
        if (normalizedEmail == "admin@legacyframes.cl" && password == "Admin123!") {
            // Asegura que exista el usuario admin en BD; si no, créalo y devuélvelo
            // (La creación/verificación principal se hace en el init del ViewModel ahora)
            val adminUser = userDao.getByEmail(normalizedEmail)
            if (adminUser != null) {
                return Result.success(adminUser)
            } else {
                // Si por alguna razón no existiera (aunque el init debería crearlo),
                // lo crea aquí como fallback y lo devuelve.
                return try {
                    val admin = UserEntity(
                        nombre = "Admin", apellido = "Legacy", phone = 12345678,
                        rut = "11111111", dv = "1", email = normalizedEmail, password = password,
                        rolId = ADMIN_ROL_ID, estadoId = ACTIVO_ESTADO_ID
                    )
                    userDao.insert(admin)
                    val inserted = userDao.getByEmail(normalizedEmail)
                    if (inserted != null) Result.success(inserted)
                    else Result.failure(IllegalStateException("No se pudo crear/recuperar el administrador"))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }

        // Login normal para otros usuarios
        val user = userDao.getByEmail(normalizedEmail)
        return if (user != null && user.password == password) { // En producción, compara hash
            // Asegurarse de que el usuario no sea el admin hardcoded si las credenciales no coinciden
            if (user.rolId == ADMIN_ROL_ID && password != "Admin123!") {
                Result.failure(IllegalArgumentException("Credenciales inválidas"))
            } else {
                Result.success(user)
            }
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // --- FUNCIÓN REGISTER (ACTUALIZADA POR TU COMPAÑERO) ---
    suspend fun register(
        nombre: String,
        apellido: String?,
        rut: String,
        dv: String,
        email: String,
        password: String,
        phone: Int,
        calle: String? = null,
        numero: String? = null,
        departamento: String? = null,
        comunaId: Int? = null
    ): Result<Long> { // Devuelve el ID del usuario insertado o un error

        // 1. Validar si el email ya existe
        val normalizedEmail = email.trim().lowercase()
        val exists = userDao.getByEmail(normalizedEmail) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        // Evitar registrarse con el email de admin
        if (normalizedEmail == "admin@legacyframes.cl") {
            return Result.failure(IllegalStateException("Email reservado para administración"))
        }


        // 2. Crear la entidad UserEntity
        val newUser = UserEntity(
            nombre = nombre.trim(),
            apellido = apellido?.trim(),
            rut = rut.trim(),
            dv = dv.trim().uppercase(),
            email = normalizedEmail,
            phone = phone,
            password = password, // Recordatorio: Usar hash en producción
            rolId = CLIENTE_ROL_ID,
            estadoId = ACTIVO_ESTADO_ID,
            calle = calle?.trim(),
            numero = numero?.trim(),
            departamento = departamento?.trim(),
            comunaId = comunaId
        )

        // 3. Intentar insertar en la base de datos
        return try {
            val id = userDao.insert(newUser)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FUNCIÓN AÑADIDA ---
    /**
     * Asegura que el usuario administrador exista en la base de datos.
     * Si no existe, lo crea con las credenciales predeterminadas.
     * Esta función es llamada desde el init del AuthViewModel.
     * Devuelve la entidad del usuario administrador (existente o recién creada).
     */
    suspend fun ensureAdminUserExists(): UserEntity {
        val adminEmail = "admin@legacyframes.cl"
        var admin = userDao.getByEmail(adminEmail)
        if (admin == null) {
            admin = UserEntity(
                nombre = "Admin",
                apellido = "Legacy",
                phone = 12345678, // Teléfono de ejemplo
                rut = "11111111", // RUT de ejemplo
                dv = "1",         // DV de ejemplo
                email = adminEmail,
                password = "Admin123!", // Contraseña (usar hash en producción)
                rolId = ADMIN_ROL_ID,
                estadoId = ACTIVO_ESTADO_ID
            )
            try {
                userDao.insert(admin)
                // Volver a obtenerlo para asegurarse de tener el ID asignado por la BD
                admin = userDao.getByEmail(adminEmail)
                if (admin == null) { // Si aún es nulo después de insertar, lanzar error
                    throw IllegalStateException("Fallo crítico al crear/recuperar usuario admin.")
                }
            } catch (e: Exception) {
                // Si falla la inserción (ej: email ya existe pero getByEmail falló antes?), intentar obtenerlo de nuevo
                admin = userDao.getByEmail(adminEmail)
                if (admin == null) {
                    throw IllegalStateException("Fallo crítico al asegurar usuario admin: ${e.message}", e)
                }
            }
        }
        // Asegurarse de que el usuario admin tenga rol de admin (podría haber sido cambiado)
        if (admin.rolId != ADMIN_ROL_ID) {
            // (Opcional: podrías forzar la actualización del rol aquí si es necesario)
            println("Advertencia: El usuario admin@legacyframes.cl no tiene rol de administrador en la BD.")
            // Considera actualizarlo: userDao.update(admin.copy(rolId = ADMIN_ROL_ID))
        }
        return admin
    }
    // ----------------------------

    // --- Otras funciones podrían ir aquí (ej: getUserById, updateProfile, etc.) ---

} // Fin de la clase UserRepository