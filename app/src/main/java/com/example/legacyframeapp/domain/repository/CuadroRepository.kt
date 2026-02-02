package com.example.legacyframeapp.domain.repository

import com.example.legacyframeapp.domain.model.Cuadro

interface CuadroRepository {
    suspend fun getCuadros(): List<Cuadro>
}
