package com.example.legacyframeapp.data.repository

import com.example.legacyframeapp.data.local.cuadro.CuadroDao
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import kotlinx.coroutines.flow.Flow

class CuadroRepository(
    private val cuadroDao: CuadroDao
) {

    fun getAllCuadros(): Flow<List<CuadroEntity>> {
        return cuadroDao.getAllCuadros()
    }

    fun getCuadrosByCategory(category: String): Flow<List<CuadroEntity>> {
        return cuadroDao.getCuadrosByCategory(category)
    }

    suspend fun getAllCategories(): List<String> {
        return cuadroDao.getAllCategories()
    }

    suspend fun insert(cuadro: CuadroEntity) {
        cuadroDao.insert(cuadro)
    }

    suspend fun update(cuadro: CuadroEntity) {
        cuadroDao.update(cuadro)
    }

    suspend fun delete(cuadro: CuadroEntity) {
        cuadroDao.delete(cuadro)
    }

    suspend fun getCuadroById(id: Long): CuadroEntity? {
        return cuadroDao.getCuadroById(id)
    }
}