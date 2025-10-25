package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.order.OrderDao
import com.example.legacyframeapp.data.local.order.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val dao: OrderDao) {
    suspend fun insert(order: OrderEntity) = dao.insert(order)
    fun getAll(): Flow<List<OrderEntity>> = dao.getAll()
}
