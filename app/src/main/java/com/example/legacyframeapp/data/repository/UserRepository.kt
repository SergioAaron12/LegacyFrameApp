package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.LoginRequest
import com.example.legacyframeapp.data.network.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userPreferences: UserPreferences
) {

    /**
     * Inicia sesión enviando credenciales a la API.
     * Si es exitoso, guarda el estado de "logueado" en el teléfono.
     */
    suspend fun login(email: String, pass: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Crear el objeto de solicitud (DTO)
                val request = LoginRequest(email = email, pass = pass)

                // 2. Llamar a la API
                val response = RetrofitClient.authService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token

                    // 3. Guardar sesión localmente
                    // Aquí guardamos el flag de logueado.
                    // (Opcional: Si agregas saveToken a UserPreferences, guárdalo aquí también)
                    userPreferences.setLoggedIn(true)

                    Result.success(true)
                } else {
                    // Manejo de error (401, 403, etc.)
                    Result.failure(Exception("Credenciales incorrectas"))
                }
            } catch (e: Exception) {
                // Error de red o servidor caído
                Result.failure(e)
            }
        }
    }

    /**
     * Registra un nuevo usuario en la API.
     */
    suspend fun register(request: RegisterRequest): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.register(request)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Función auxiliar para cerrar sesión
    suspend fun logout() {
        userPreferences.setLoggedIn(false)
        // userPreferences.clearToken() // Si implementas el borrado de token
    }
}