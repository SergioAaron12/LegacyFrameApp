package com.example.legacyframeapp

import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.repository.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {

    private val cartDao = mockk<CartDao>(relaxed = true)
    private lateinit var repository: CartRepository

    @Before
    fun setup() {
        repository = CartRepository(cartDao)
    }

    @Test
    fun `addOrIncrement inserta item nuevo si no existe`() = runBlocking {
        // GIVEN: El DAO dice que no existe ese producto (retorna null)
        coEvery { cartDao.findByTypeAndRef("product", 1) } returns null

        // WHEN: Agregamos un producto
        repository.addOrIncrement("product", 1, "Marco", 1000, "img.jpg")

        // THEN: Verificamos que llamó a INSERT
        coVerify { cartDao.insert(any()) }
        // Verificamos que NO llamó a UPDATE
        coVerify(exactly = 0) { cartDao.update(any()) }
    }

    @Test
    fun `addOrIncrement actualiza cantidad si item ya existe`() = runBlocking {
        // GIVEN: El DAO dice que YA existe el producto con cantidad 1
        val existingItem = CartItemEntity(
            id = 5, type = "product", refId = 1, name = "Marco", price = 1000, quantity = 1
        )
        coEvery { cartDao.findByTypeAndRef("product", 1) } returns existingItem

        // WHEN: Lo agregamos de nuevo
        repository.addOrIncrement("product", 1, "Marco", 1000, "img.jpg")

        // THEN: Verificamos que llamó a UPDATE
        coVerify {
            // Verificamos que actualizó el objeto con cantidad 2 (1 + 1)
            cartDao.update(match { it.quantity == 2 })
        }
        // Verificamos que NO llamó a INSERT
        coVerify(exactly = 0) { cartDao.insert(any()) }
    }
}