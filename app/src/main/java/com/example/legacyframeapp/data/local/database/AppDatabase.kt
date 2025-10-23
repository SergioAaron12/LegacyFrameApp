package com.example.legacyframeapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.legacyframeapp.data.local.user.UserDao
import com.example.legacyframeapp.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- Constantes para IDs de Rol y Estado  ---
private const val ADMIN_ROL_ID = 1
private const val ACTIVO_ESTADO_ID = 1
// ------------------------------------------------------------------------------------

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
                    // --- REINTRODUCIMOS EL CALLBACK PARA INSERTAR EL ADMIN ---
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Usamos una corrutina para insertar en un hilo separado
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).userDao()

                                // Verifica si la tabla está vacía antes de insertar
                                if (dao.count() == 0) {
                                    // --- DEFINE AQUÍ TU USUARIO ADMINISTRADOR ---
                                    val adminUser = UserEntity(
                                        nombre = "Admin",
                                        apellido = "Legacy", // Opcional
                                        phone = 12345678,
                                        rut = "11111111",     // RUT de ejemplo
                                        dv = "1",            // DV de ejemplo
                                        email = "admin@legacyframes.cl", // Email del admin
                                        password = "Admin123!", // Contraseña
                                        rolId = ADMIN_ROL_ID,      // Asigna rol de Administrador
                                        estadoId = ACTIVO_ESTADO_ID   // Asigna estado Activo
                                        // Los campos de dirección quedan null por defecto
                                    )
                                    // Inserta el usuario administrador
                                    dao.insert(adminUser)
                                    println("Usuario Administrador insertado en la base de datos local.")
                                }
                            }
                        }
                    })
                    // --------------------------------------------------------
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}