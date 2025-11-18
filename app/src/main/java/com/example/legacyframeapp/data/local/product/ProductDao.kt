package com.example.legacyframeapp.data.local.product
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Usamos Flow para que la lista en MoldurasScreen se actualice sola
    // cuando el Admin agregue o elimine un producto.
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

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

    // Buscar por nombre (para actualizaciones puntuales por catálogo)
    @Query("SELECT * FROM products WHERE name = :name LIMIT 1")
    suspend fun getProductByName(name: String): ProductEntity?

    // Eliminar todos los productos (para reemplazar catálogo)
    @Query("DELETE FROM products")
    suspend fun deleteAll()
}