package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.domain.model.Cuadro

class CuadroRepository {

    // Este repositorio ahora actúa como un placeholder o gestor futuro para la API.
    // Actualmente, el AuthViewModel obtiene los "Cuadros" filtrando la lista completa de Productos (Molduras + Cuadros)
    // que viene del ProductRepository, por lo que este archivo queda limpio de lógica de base de datos.

    suspend fun insert(cuadro: Cuadro) {
        // TODO: En el futuro, aquí llamarías a RetrofitClient.productService.createProduct(...)
    }

    suspend fun delete(cuadro: Cuadro) {
        // TODO: En el futuro, aquí llamarías a RetrofitClient.productService.deleteProduct(...)
    }

    suspend fun getAllCategories(): List<String> {
        // Retornamos categorías estáticas o vacías, ya que la API principal se encarga de esto
        return emptyList()
    }
}