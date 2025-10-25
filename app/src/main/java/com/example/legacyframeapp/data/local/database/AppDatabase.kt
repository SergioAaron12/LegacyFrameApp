package com.example.legacyframeapp.data.local.database // Tu paquete original

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
// --- ¡AÑADIR ESTOS IMPORTS! ---
import com.example.legacyframeapp.data.local.product.ProductDao
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.data.local.cuadro.CuadroDao
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.local.cart.CartDao
import com.example.legacyframeapp.data.local.order.OrderEntity
import com.example.legacyframeapp.data.local.order.OrderDao
// -------------------------------
import com.example.legacyframeapp.data.local.user.UserDao
import com.example.legacyframeapp.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- (Tus constantes ADMIN_ROL_ID, etc. van aquí) ---
private const val ADMIN_ROL_ID = 1
private const val ACTIVO_ESTADO_ID = 1
// ----------------------------------------------------

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,  // <--- 1. ASEGÚRATE DE AÑADIR ESTO
        CuadroEntity::class,   // <--- AÑADIR CUADROS
        CartItemEntity::class,  // <--- Carrito
        OrderEntity::class      // <--- Órdenes / compras
    ],
    version = 8,              // bump version for new test user
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    // --- 3. ESTA LÍNEA ES LA QUE TE FALTA Y CAUSA EL ERROR ---
    abstract fun productDao(): ProductDao
    abstract fun cuadroDao(): CuadroDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    // ---------------------------------------------------------

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "legacy_frames_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Inserta el usuario admin y datos de ejemplo en un hilo de IO
                        CoroutineScope(Dispatchers.IO).launch {
                            val dbi = getDatabase(context)
                            val userDao = dbi.userDao()
                            val productDao = dbi.productDao()
                            val cuadroDao = dbi.cuadroDao()

                            if (userDao.count() == 0) { // Solo si la BD está vacía
                                // Usuario Admin
                                val adminUser = UserEntity(
                                    nombre = "Admin",
                                    apellido = "Legacy",
                                    phone = 12345678,
                                    rut = "11111111",
                                    dv = "1",
                                    email = "admin@legacyframes.cl",
                                    password = "Admin123!",
                                    rolId = ADMIN_ROL_ID,
                                    estadoId = ACTIVO_ESTADO_ID
                                )
                                userDao.insert(adminUser)
                                
                                // Usuario de prueba
                                val testUser = UserEntity(
                                    nombre = "Usuario",
                                    apellido = "Prueba",
                                    phone = 87654321,
                                    rut = "22222222",
                                    dv = "2",
                                    email = "usuario@legacyframes.cl",
                                    password = "User123!",
                                    rolId = 2, // Rol de usuario normal
                                    estadoId = ACTIVO_ESTADO_ID
                                )
                                userDao.insert(testUser)
                            }

                            // Productos de ejemplo (molduras)
                            try {
                                productDao.insert(
                                    ProductEntity(name = "I 09 greca zo", description = "Elegante greca decorativa con diseño tradicional ZO.", price = 37500, category = "grecas", imagePath = "moldura1")
                                )
                                productDao.insert(
                                    ProductEntity(name = "I 09 greca corazón", description = "Greca con motivo de corazón, perfecta para marcos románticos.", price = 40000, category = "grecas", imagePath = "moldura2")
                                )
                                productDao.insert(
                                    ProductEntity(name = "P 15 greca LA oro", description = "Greca con acabado dorado, elegante y sofisticada.", price = 24000, category = "grecas", imagePath = "moldura3")
                                )
                                // Nuevos productos con imágenes locales
                                productDao.insert(
                                    ProductEntity(
                                        name = "P 15 greca LA plata",
                                        description = "Greca con acabado plateado, moderna y elegante.",
                                        price = 20000,
                                        category = "Grecas",
                                        imagePath = "p15_greca_plata"
                                    )
                                )
                                productDao.insert(
                                    ProductEntity(
                                        name = "H 20 albayalde azul",
                                        description = "Moldura rústica con acabado albayalde azul, ideal para ambientes campestres.",
                                        price = 20000,
                                        category = "Rústicas",
                                        imagePath = "h20_albayalde_azul"
                                    )
                                )
                                productDao.insert(
                                    ProductEntity(
                                        name = "B-10 t/alerce",
                                        description = "Moldura natural de alerce con textura original de la madera.",
                                        price = 20000,
                                        category = "Naturales",
                                        imagePath = "b10_alerce"
                                    )
                                )
                                productDao.insert(
                                    ProductEntity(
                                        name = "J-16",
                                        description = "Moldura de madera nativa chilena, resistente y de gran calidad.",
                                        price = 20000,
                                        category = "Nativas",
                                        imagePath = "j16_nativa"
                                    )
                                )
                                productDao.insert(
                                    ProductEntity(
                                        name = "P-12 Finger Joint",
                                        description = "Moldura finger joint de alta calidad con unión invisible.",
                                        price = 20000,
                                        category = "Finger Joint",
                                        imagePath = "p12_finger_joint"
                                    )
                                )
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
                                        imagePath = "",
                                        isCustom = false,
                                        artist = "Legacy Studio"
                                    )
                                )
                            } catch (_: Exception) {}
                        }
                    }
                })
                // --- 4. ASEGÚRATE QUE ESTA LÍNEA ESTÉ (para la migración) ---
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}