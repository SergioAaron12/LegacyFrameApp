package com.example.legacyframeapp.data.repository

import android.util.Log
import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.OrderRequest
import com.example.legacyframeapp.domain.model.Order

class OrderRepository {

    suspend fun createOrder(email: String, request: OrderRequest): Result<Boolean> {
        return try {
            val response = RetrofitClient.orderService.createOrder(email, request)
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyOrders(email: String): List<Order> {
        return try {
            val response = RetrofitClient.orderService.getMyOrders(email)
            if (response.isSuccessful) {
                val remotes = response.body() ?: emptyList()
                remotes.map { remote ->
                    val resumen = remote.detalles?.joinToString("\n") {
                        "- ${it.nombreProducto} x${it.cantidad}"
                    } ?: "Sin detalles"

                    Order(
                        id = remote.id,
                        dateMillis = System.currentTimeMillis(),
                        itemsText = resumen,
                        total = remote.total.toInt()
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OrderRepo", "Error historial: ${e.message}")
            emptyList()
        }
    }
}