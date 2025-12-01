package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.OrderRequest
import com.example.legacyframeapp.domain.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OrderRepository {

    // Envía el pedido a la API (POST)
    suspend fun createOrder(email: String, request: OrderRequest): Result<Boolean> {
        return try {
            val response = RetrofitClient.orderService.createOrder(email, request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtiene el historial. Por ahora devolvemos lista vacía para que no falle la UI
    // (Aquí conectarías con GET /api/orders/my-orders en el futuro)
    fun getAll(): Flow<List<Order>> {
        return flowOf(emptyList())
    }
}