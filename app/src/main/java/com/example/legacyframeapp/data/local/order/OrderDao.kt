package com.example.legacyframeapp.data.local.order

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(order: OrderEntity)

    @Query("SELECT * FROM orders ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<OrderEntity>>
}
