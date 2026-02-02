package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(private val cartDao: CartDao) {

    // Obtener todos los items del carrito (Flow para actualización en tiempo real)
    fun items(): Flow<List<CartItemEntity>> = cartDao.getAllItems()

    // Contar total de productos
    fun count(): Flow<Int> = cartDao.getItemCount()

    // Calcular precio total
    fun total(): Flow<Int> = cartDao.getTotalPrice().map { it ?: 0 }

    // Agregar producto o aumentar cantidad si ya existe
    suspend fun addOrIncrement(
        type: String,
        refId: Long,
        name: String,
        price: Int,
        image: String // Este es el dato que llega
    ) {
        val existingItem = cartDao.getItemByRef(type, refId)

        if (existingItem != null) {
            // Si ya existe, aumentamos la cantidad +1
            val updated = existingItem.copy(quantity = existingItem.quantity + 1)
            cartDao.update(updated)
        } else {
            // Si es nuevo, lo creamos
            // AQUÍ ESTABA EL ERROR: Ahora usamos 'imageUrl'
            val newItem = CartItemEntity(
                type = type,
                refId = refId,
                name = name,
                price = price,
                imageUrl = image, // Asignamos la imagen entrante al campo correcto
                quantity = 1
            )
            cartDao.insert(newItem)
        }
    }

    suspend fun updateQuantity(item: CartItemEntity, newQuantity: Int) {
        if (newQuantity > 0) {
            cartDao.update(item.copy(quantity = newQuantity))
        }
    }

    suspend fun remove(item: CartItemEntity) {
        cartDao.delete(item)
    }

    suspend fun clear() {
        cartDao.clear()
    }
}