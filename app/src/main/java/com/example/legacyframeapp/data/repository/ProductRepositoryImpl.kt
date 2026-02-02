package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.network.ProductApiService
import com.example.legacyframeapp.domain.model.Product
import com.example.legacyframeapp.domain.repository.ProductRepository

class ProductRepositoryImpl(private val apiService: ProductApiService) : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        val response = apiService.getProducts()
        return if (response.isSuccessful) {
            response.body()?.map { dto ->
                Product(
                    id = dto.id.toString(),
                    name = dto.nombre,
                    description = dto.descripcion ?: "",
                    price = dto.precio.toInt(),
                    imageUrl = dto.imagenUrl ?: "",
                    category = dto.categoria?.nombre ?: "Sin categoría"
                )
            } ?: emptyList()
        } else {
            emptyList() // Manejar error
        }
    }
}
