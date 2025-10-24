package com.example.legacyframeapp.data.local.database // Tu paquete original

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
// --- ¡AÑADIR ESTOS IMPORTS! ---
import com.example.legacyframeapp.data.local.product.ProductDao
import com.example.legacyframeapp.data.local.product.ProductEntity
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
        ProductEntity::class  // <--- 1. ASEGÚRATE DE AÑADIR ESTO
    ],
    version = 2,              // <--- 2. ASEGÚRATE DE CAMBIAR LA VERSIÓN A 2
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    // --- 3. ESTA LÍNEA ES LA QUE TE FALTA Y CAUSA EL ERROR ---
    abstract fun productDao(): ProductDao
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
                        // ... (Tu callback para crear el Admin sigue igual aquí) ...
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Inserta el usuario admin en un hilo separado
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).userDao()
                                if (dao.count() == 0) { // Solo si la BD está vacía
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
                                    dao.insert(adminUser)
                                }
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