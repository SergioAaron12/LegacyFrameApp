package com.example.legacyframeapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.legacyframeapp.data.local.product.ProductDao
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.data.local.cuadro.CuadroDao
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.order.OrderEntity
import com.example.legacyframeapp.data.local.order.OrderDao
import com.example.legacyframeapp.data.local.user.UserDao
import com.example.legacyframeapp.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- Constantes para roles y estados ---
private const val ADMIN_ROL_ID = 1
private const val ACTIVO_ESTADO_ID = 1
// ------------------------------------

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CuadroEntity::class,
        CartItemEntity::class,
        OrderEntity::class
    ],
    // --- ¡IMPORTANTE! Incrementa la versión si cambiaste entidades ---
    version = 4, // O el número siguiente al que tenías
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cuadroDao(): CuadroDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "legacyframe_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Llenar con datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                val productDao = database.productDao()
                                val cuadroDao = database.cuadroDao()

                                // Productos de ejemplo (usa solo nombres de drawable)
                                try {
                                    productDao.insert(ProductEntity(
                                        name = "I 09 greca zo",
                                        description = "Elegante greca decorativa",
                                        price = 37500,
                                        category = "Grecas",
                                        imagePath = "moldura1"))
                                    productDao.insert(ProductEntity(
                                        name = "I 09 greca corazón",
                                        description = "Greca con motivo de corazón",
                                        price = 32000,
                                        category = "Grecas",
                                        imagePath = "moldura2"))
                                    productDao.insert(ProductEntity(
                                        name = "P 15 greca LA oro",
                                        description = "Greca con acabado dorado",
                                        price = 20000,
                                        category = "Grecas",
                                        imagePath = "moldura3"))
                                    productDao.insert(ProductEntity(
                                        name = "P 15 greca LA plata",
                                        description = "Greca con acabado plateado",
                                        price = 20000,
                                        category = "Grecas",
                                        imagePath = "p15_greca_plata"))
                                    productDao.insert(ProductEntity(
                                        name = "H 20 albayalde azul",
                                        description = "Moldura rústica azul",
                                        price = 20000,
                                        category = "Rústicas",
                                        imagePath = "h20_albayalde_azul"))
                                    productDao.insert(ProductEntity(
                                        name = "B-10 t/alerce",
                                        description = "Moldura natural de alerce",
                                        price = 20000,
                                        category = "Naturales",
                                        imagePath = "b10_alerce"))
                                    productDao.insert(ProductEntity(
                                        name = "J-16",
                                        description = "Moldura de madera nativa",
                                        price = 20000,
                                        category = "Nativas",
                                        imagePath = "j16_nativa"))
                                    productDao.insert(ProductEntity(
                                        name = "P-12 Finger Joint",
                                        description = "Moldura finger joint",
                                        price = 20000,
                                        category = "Finger Joint",
                                        imagePath = "p12_finger_joint"))
                                } catch (_: Exception) {}

                                // Cuadros de ejemplo
                                try {
                                    cuadroDao.insert(
                                        CuadroEntity(
                                            title = "Paisaje Cordillera",
                                            description = "Fotografía profesional enmarcada.",
                                            price = 65000,
                                            size = "50x70 cm",
                                            material = "Canvas",
                                            category = "Paisajes",
                                            // --- ¡CORREGIDO AQUÍ! ---
                                            imagePath = "paisaje", // Solo el nombre, sin extensión
                                            // -------------------------
                                            isCustom = false,
                                            artist = "Legacy Studio"
                                        )
                                    )
                                } catch (_: Exception) {}
                            }
                        }
                    })
                    // Permite destruir y recrear la BD si las migraciones fallan
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}