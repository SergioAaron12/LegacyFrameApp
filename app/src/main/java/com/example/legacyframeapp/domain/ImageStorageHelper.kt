package com.example.legacyframeapp.domain
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

object ImageStorageHelper {

    /**
     * @param context Contexto de la aplicación.
     * @param uri El Uri de la imagen seleccionada (ej: "content://...")
     * @return El nombre único del archivo guardado (ej: "product_1678886400.jpg")
     * @throws IOException Si la copia falla.
     */
    suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            // 1. Genera un nombre de archivo único
            val fileName = "product_${System.currentTimeMillis()}.jpg"

            // 2. Abre el flujo de entrada desde el Uri (la galería)
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("No se pudo abrir el stream desde el Uri: $uri")

            // 3. Define el archivo de salida en el almacenamiento interno
            // 'filesDir' es la carpeta privada de tu app
            val outputFile = File(context.filesDir, fileName)

            // 4. Abre el flujo de salida al nuevo archivo
            val outputStream = outputFile.outputStream()

            // 5. Copia los bytes y cierra los flujos
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // 6. Devuelve solo el nombre del archivo
            fileName
        }
    }

    /**
     * Obtiene el archivo de imagen desde el almacenamiento interno.
     *
     * @param context Contexto de la aplicación.
     * @param fileName El nombre del archivo (el que guardamos en la DB).
     * @return Un objeto File que apunta a la imagen guardada.
     */
    fun getImageFile(context: Context, fileName: String): File {
        return File(context.filesDir, fileName)
    }
}