package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.network.ProductApiService
import com.example.legacyframeapp.domain.model.Cuadro
import com.example.legacyframeapp.domain.repository.CuadroRepository

class CuadroRepositoryImpl(private val apiService: ProductApiService) : CuadroRepository {

    override suspend fun getCuadros(): List<Cuadro> {
        val response = apiService.getProducts()
        return if (response.isSuccessful) {
            response.body()?.filter { it.categoria?.nombre?.lowercase() == "cuadros" }?.map { dto ->
                Cuadro(
                    id = dto.id.toString(),
                    title = dto.nombre,
                    description = dto.descripcion ?: "",
                    price = dto.precio.toInt(),
                    imageUrl = dto.imagenUrl ?: "",
                    category = dto.categoria?.nombre ?: "Cuadros"
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}
