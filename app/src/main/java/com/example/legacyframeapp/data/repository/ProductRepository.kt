package com.example.legacyframeapp.data.repository

import android.util.Log
import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.CreateProductRequest
import com.example.legacyframeapp.data.network.model.ProductRemote
import com.example.legacyframeapp.domain.model.Product

class ProductRepository {

    companion object {
        // ⚠️ IMPORTANTE: Pega aquí tu link del puerto 8083 de VS Code (sin barra al final)
        private const val BASE_IMAGE_URL = "https://tu-link-prod-8083.devtunnels.ms"
    }

    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = RetrofitClient.productService.getProducts()

            if (response.isSuccessful) {
                val remotes: List<ProductRemote> = response.body() ?: emptyList()

                remotes.map { remote ->
                    val rawUrl = remote.imagenUrl ?: ""
                    val fixedUrl = if (rawUrl.startsWith("/")) {
                        BASE_IMAGE_URL + rawUrl
                    } else {
                        rawUrl
                    }

                    Product(
                        id = remote.id,
                        name = remote.nombre,
                        description = remote.descripcion ?: "",
                        price = remote.precio.toInt(),
                        imageUrl = fixedUrl,
                        category = remote.categoria?.nombre ?: "Sin categoría"
                    )
                }
            } else {
                Log.e("ProductRepo", "Error API: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepo", "Error red: ${e.message}")
            emptyList()
        }
    }

    suspend fun createProduct(request: CreateProductRequest): Boolean {
        return try {
            val response = RetrofitClient.productService.createProduct(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ProductRepo", "Error crear: ${e.message}")
            false
        }
    }

    suspend fun getProductById(id: Long): Product? {
        return getAllProducts().find { it.id == id }
    }

    suspend fun deleteProduct(id: Long): Boolean { return true }
}