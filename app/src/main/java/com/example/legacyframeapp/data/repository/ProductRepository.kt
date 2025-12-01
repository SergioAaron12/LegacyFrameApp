package com.example.legacyframeapp.data.repository

import android.util.Log
import com.example.legacyframeapp.data.network.RetrofitClient
// Importamos el modelo de la API (Datos crudos en español)
import com.example.legacyframeapp.data.network.model.ProductRemote
// Importamos el modelo del Dominio/UI (Datos limpios en inglés)
import com.example.legacyframeapp.domain.model.Product

class ProductRepository {

    suspend fun getAllProducts(): List<Product> {
        // En Kotlin, try-catch devuelve el valor de la última línea
        return try {
            val response = RetrofitClient.productService.getProducts()

            if (response.isSuccessful) {
                // 1. Obtenemos la lista cruda y forzamos el tipo para evitar errores de inferencia
                val remotes: List<ProductRemote> = response.body() ?: emptyList()

                // 2. Transformamos cada 'remote' (API) en un 'Product' (UI)
                val domainList: List<Product> = remotes.map { remote ->
                    Product(
                        id = remote.id,
                        name = remote.nombre, // Ahora sí reconoce 'nombre'
                        description = remote.descripcion ?: "",
                        price = remote.precio.toInt(),
                        imageUrl = remote.imagenUrl ?: "",
                        category = remote.categoria?.nombre ?: "Sin categoría"
                    )
                }
                domainList // Devolvemos la lista transformada
            } else {
                Log.e("ProductRepo", "Error API: ${response.code()}")
                emptyList<Product>() // Especificamos <Product> explícitamente
            }
        } catch (e: Exception) {
            Log.e("ProductRepo", "Error red: ${e.message}")
            emptyList<Product>() // Especificamos <Product> explícitamente
        }
    }

    // Función auxiliar opcional para obtener por ID filtrando la lista
    suspend fun getProductById(id: Long): Product? {
        return getAllProducts().find { it.id == id }
    }
}