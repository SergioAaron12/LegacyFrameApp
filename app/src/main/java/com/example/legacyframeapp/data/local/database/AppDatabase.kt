package com.example.legacyframeapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.cart.CartItemEntity

@Database(
    entities = [CartItemEntity::class], // Solo mantenemos la tabla del Carrito
    version = 5, // Aumentamos la versión para forzar la actualización
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // El único DAO que necesitamos ahora
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "legacyframe_db_v2" // Cambiamos el nombre para crear una base de datos limpia desde cero
                )
                    .fallbackToDestructiveMigration() // Si hay conflictos de versión, reconstruye la DB (seguro porque el carrito es temporal)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}