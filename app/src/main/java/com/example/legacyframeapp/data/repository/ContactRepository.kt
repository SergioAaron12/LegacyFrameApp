package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.network.ContactApiService
import com.example.legacyframeapp.data.network.model.ContactRequest

class ContactRepository(private val apiService: ContactApiService) {
    suspend fun sendMessage(nombre: String, email: String, mensaje: String): Result<Boolean> {
        return try {
            val request = ContactRequest(nombre, email, mensaje)
            val response = apiService.sendContact(request)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al enviar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}