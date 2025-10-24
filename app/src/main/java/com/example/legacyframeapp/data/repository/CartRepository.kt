package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(
    private val cartDao: CartDao
) {
    fun items(): Flow<List<CartItemEntity>> = cartDao.getItems()

    fun total(): Flow<Int> = items().map { list -> list.sumOf { it.price * it.quantity } }

    fun count(): Flow<Int> = items().map { list -> list.sumOf { it.quantity } }

    suspend fun addOrIncrement(type: String, refId: Long, name: String, price: Int, imagePath: String?) {
        val existing = cartDao.findByTypeAndRef(type, refId)
        if (existing != null) {
            cartDao.update(existing.copy(quantity = existing.quantity + 1))
        } else {
            cartDao.insert(
                CartItemEntity(
                    type = type,
                    refId = refId,
                    name = name,
                    price = price,
                    imagePath = imagePath,
                    quantity = 1
                )
            )
        }
    }

    suspend fun updateQuantity(item: CartItemEntity, newQty: Int) {
        if (newQty <= 0) {
            cartDao.delete(item)
        } else {
            cartDao.update(item.copy(quantity = newQty))
        }
    }

    suspend fun remove(item: CartItemEntity) = cartDao.delete(item)

    suspend fun clear() = cartDao.clear()
}
