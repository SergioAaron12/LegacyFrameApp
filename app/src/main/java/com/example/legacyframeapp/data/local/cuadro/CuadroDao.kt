package com.example.legacyframeapp.data.local.cuadro

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CuadroDao {

    @Query("SELECT * FROM cuadros ORDER BY title ASC")
    fun getAllCuadros(): Flow<List<CuadroEntity>>

    @Query("SELECT * FROM cuadros WHERE category = :category ORDER BY title ASC")
    fun getCuadrosByCategory(category: String): Flow<List<CuadroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cuadro: CuadroEntity)

    @Update
    suspend fun update(cuadro: CuadroEntity)

    @Delete
    suspend fun delete(cuadro: CuadroEntity)

    @Query("SELECT * FROM cuadros WHERE id = :id LIMIT 1")
    suspend fun getCuadroById(id: Long): CuadroEntity?

    @Query("SELECT DISTINCT category FROM cuadros ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
}