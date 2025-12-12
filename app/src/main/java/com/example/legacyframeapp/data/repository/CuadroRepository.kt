package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.domain.model.Cuadro

class CuadroRepository {

    suspend fun insert(cuadro: Cuadro) {
    }

    suspend fun delete(cuadro: Cuadro) {
    }

    suspend fun getAllCategories(): List<String> {
        // Retornamos categorías estáticas o vacías, ya que la API principal se encarga de esto
        return emptyList()
    }
}