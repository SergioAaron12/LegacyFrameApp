package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.*

class UserRepository(private val userPreferences: UserPreferences) {

    // --- LOGIN ---
    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            // CORRECCIÓN AQUÍ: Usamos 'pass =' en lugar de 'password ='
            val request = LoginRequest(email = email, pass = pass)

            val response = RetrofitClient.authService.login(request)

            if (response.isSuccessful) {
                val token = response.body()?.token ?: ""
                // Guardamos Token y Email
                userPreferences.saveToken(token)
                userPreferences.saveEmail(email)
                userPreferences.setLoggedIn(true) // Aseguramos que se marque como logueado
                Result.success(token)
            } else {
                Result.failure(Exception("Error Login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- REGISTRO ---
    suspend fun register(request: RegisterRequest): Result<Boolean> {
        return try {
            val response = RetrofitClient.authService.register(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error Registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- CERRAR SESIÓN ---
    suspend fun logout() {
        userPreferences.clear()
    }

    // --- OBTENER PERFIL ---
    suspend fun getProfile(email: String): Result<UserProfileResponse> {
        return try {
            val response = RetrofitClient.authService.getProfile(email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ACTUALIZAR PERFIL ---
    suspend fun updateProfile(req: UpdateProfileRequest): Result<Boolean> {
        return try {
            val response = RetrofitClient.authService.updateProfile(req)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al actualizar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}