package com.example.legacyframeapp.data.local.product
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow // Importante usar Flow

@Dao
interface ProductDao {

    // Usamos Flow para que la lista en MoldurasScreen se actualice sola
    // cuando el Admin agregue o elimine un producto.
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    // Funciones para el Admin (CRUD)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    // (Opcional) Una función para buscar por ID, útil para la pantalla de "Editar"
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?
}