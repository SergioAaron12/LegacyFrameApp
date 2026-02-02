package com.example.legacyframeapp.data.repository

import android.util.Log
import com.example.legacyframeapp.data.network.ProductApiService
import com.example.legacyframeapp.data.network.model.CreateProductRequest
import com.example.legacyframeapp.data.network.model.ProductRemote
import com.example.legacyframeapp.domain.model.Product

class ProductRepository(private val apiService: ProductApiService) : com.example.legacyframeapp.domain.repository.ProductRepository {

    companion object {
        // Tu servidor (Emulador o DevTunnels)
        private const val BASE_IMAGE_URL = "http://10.0.2.2:8083"
    }

    override suspend fun getProducts(): List<Product> {
        return try {
            val response = apiService.getProducts()

            if (response.isSuccessful) {
                val remotes: List<ProductRemote> = response.body() ?: emptyList()

                remotes.map { remote ->
                    val pathDesdeBd = remote.imagenUrl ?: ""

                    // --- AQUÍ ESTÁ EL TRUCO ---
                    // Si empieza con 'content' o 'file', es una foto local del celular -> La dejamos igual.
                    // Si empieza con 'http', es internet -> La dejamos igual.
                    // Si no, es una imagen del servidor (/assets) -> Le ponemos el servidor antes.

                    val finalUrl = when {
                        pathDesdeBd.startsWith("content://") -> pathDesdeBd
                        pathDesdeBd.startsWith("file://") -> pathDesdeBd
                        pathDesdeBd.startsWith("http") -> pathDesdeBd
                        pathDesdeBd.isBlank() -> ""
                        else -> {
                            val pathLimpio = if (pathDesdeBd.startsWith("/")) pathDesdeBd else "/$pathDesdeBd"
                            BASE_IMAGE_URL + pathLimpio
                        }
                    }

                    Product(
                        id = remote.id.toString(),
                        name = remote.nombre,
                        description = remote.descripcion ?: "",
                        price = remote.precio.toInt(),
                        imageUrl = finalUrl,
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
            val response = apiService.createProduct(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteProduct(id: String): Boolean {
        return try {
            val response = apiService.deleteProduct(id.toLong())
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getProductById(id: String): Product? {
        return getProducts().find { it.id == id }
    }
}