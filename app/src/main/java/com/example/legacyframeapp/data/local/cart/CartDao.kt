package com.example.legacyframeapp.data.local.cart
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Obtiene todos los items del carrito. Usamos Flow para que la UI se actualice sola.
    @Query("SELECT * FROM cart_items ORDER BY productName ASC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    // Obtiene un item específico usando el ID del PRODUCTO (para saber si ya existe)
    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getItemByProductId(productId: Long): CartItemEntity?

    // Inserta un nuevo item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    // Actualiza un item (ej: para cambiar la cantidad)
    @Update
    suspend fun update(item: CartItemEntity)

    // Borra un item específico
    @Delete
    suspend fun delete(item: CartItemEntity)

    // Vacía todo el carrito (para cuando se "compre")
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- (Opcional) Funciones para obtener totales directamente ---

    // Cuenta el número total de unidades en el carrito (ej: 2 marcos A + 1 marco B = 3)
    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getCartItemCount(): Flow<Int?> // Flow<Int?> porque puede ser 0 o null

    // Calcula el precio total del carrito
    @Query("SELECT SUM(quantity * productPrice) FROM cart_items")
    fun getCartTotal(): Flow<Int?> // Flow<Int?> porque puede ser 0 o null
}