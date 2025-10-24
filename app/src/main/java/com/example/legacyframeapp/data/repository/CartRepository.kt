package com.example.legacyframeapp.data.repository
import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(
    private val cartDao: CartDao
) {

    // --- Flujos para la UI (Total, Conteo de Items) ---

    // Flujo de todos los items en el carrito
    fun getAllCartItems(): Flow<List<CartItemEntity>> {
        return cartDao.getAllCartItems()
    }

    // Flujo del número total de unidades en el carrito (devuelve 0 si es null)
    val cartItemCount: Flow<Int> = cartDao.getCartItemCount().map { it ?: 0 }

    // Flujo del precio total del carrito (devuelve 0 si es null)
    val cartTotal: Flow<Int> = cartDao.getCartTotal().map { it ?: 0 }


    // --- Acciones del Carrito ---

    /**
     * Lógica principal para añadir un producto al carrito.
     * Si el producto ya existe, incrementa la cantidad.
     * Si no existe, lo inserta como un nuevo item.
     */
    suspend fun addToCart(product: ProductEntity) {
        // 1. Busca si el item ya existe usando el ID del PRODUCTO
        val existingItem = cartDao.getItemByProductId(product.id)

        if (existingItem != null) {
            // 2. Si existe, crea una copia actualizada con la cantidad + 1
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + 1
            )
            cartDao.update(updatedItem)
        } else {
            // 3. Si no existe, crea un nuevo CartItemEntity
            val newItem = CartItemEntity(
                productId = product.id,
                quantity = 1,
                // Copiamos los datos del producto
                productName = product.name,
                productPrice = product.price,
                productImagePath = product.imagePath
            )
            cartDao.insert(newItem)
        }
    }

    // Actualiza un item (para + o - desde la pantalla del carrito)
    suspend fun update(item: CartItemEntity) {
        cartDao.update(item)
    }

    // Elimina un item
    suspend fun delete(item: CartItemEntity) {
        cartDao.delete(item)
    }

    // Vacía el carrito
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
