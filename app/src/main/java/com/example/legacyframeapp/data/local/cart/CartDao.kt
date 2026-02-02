package com.example.legacyframeapp.data.local.cart

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    // Obtener todos los items (Reactivo)
    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartItemEntity>>

    // Contar la cantidad total de productos (Suma de 'quantity')
    // Usamos COALESCE para que devuelva 0 si está vacío en vez de null
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_items")
    fun getItemCount(): Flow<Int>

    // Calcular el precio total (Suma de price * quantity)
    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getTotalPrice(): Flow<Int?>

    // Buscar un producto específico (para ver si ya existe y sumar +1)
    @Query("SELECT * FROM cart_items WHERE type = :type AND refId = :refId LIMIT 1")
    suspend fun getItemByRef(type: String, refId: Long): CartItemEntity?

    // Insertar nuevo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    // Actualizar (cantidad)
    @Update
    suspend fun update(item: CartItemEntity)

    // Borrar uno
    @Delete
    suspend fun delete(item: CartItemEntity)

    // Vaciar carrito
    @Query("DELETE FROM cart_items")
    suspend fun clear(): Unit
}