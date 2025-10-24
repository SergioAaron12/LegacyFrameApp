package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.product.ProductDao
import com.example.legacyframeapp.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao
) {

    // Flujo de todos los productos (para la lista principal)
    fun getAllProducts(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

    // --- Funciones de Admin ---

    suspend fun insert(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun update(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun delete(product: ProductEntity) {
        productDao.delete(product)
    }

    suspend fun getProductById(id: Long): ProductEntity? {
        return productDao.getProductById(id)
    }
}