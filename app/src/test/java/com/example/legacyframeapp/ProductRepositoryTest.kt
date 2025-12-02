package com.example.legacyframeapp

import com.example.legacyframeapp.data.network.ProductApiService
import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.CategoriaRemote
import com.example.legacyframeapp.data.network.model.ProductRemote
import com.example.legacyframeapp.data.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ProductRepositoryTest {

    // 1. Mockeamos el servicio de la API
    private val apiServiceMock = mockk<ProductApiService>()
    private lateinit var repository: ProductRepository

    @Before
    fun setup() {
        // --- MOCKEO DE RETROFIT ---
        // Simulamos el objeto Singleton para que devuelva nuestro servicio falso
        mockkObject(RetrofitClient)
        coEvery { RetrofitClient.productService } returns apiServiceMock

        // --- SOLUCIÓN AL ERROR "Method e not mocked" ---
        // Simulamos la clase estática Log de Android
        mockkStatic(android.util.Log::class)
        // Le decimos que devuelva 0 cuando se llame a cualquier Log.e
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

        // Inicializamos el repositorio que vamos a probar
        repository = ProductRepository()
    }

    @After
    fun tearDown() {
        unmockkAll() // Limpiamos los mocks después de cada test
    }

    @Test
    fun `getAllProducts devuelve lista mapeada correctamente cuando API responde 200`() = runBlocking {
        // GIVEN: La API responde una lista de productos remotos (formato del backend)
        val fakeApiData = listOf(
            ProductRemote(
                id = 1,
                nombre = "Marco Test",
                descripcion = "Descripción de prueba",
                precio = 5000.0,
                stock = 10,
                imagenUrl = "http://fake.url/img.jpg",
                categoria = CategoriaRemote(1, "Grecas", null)
            )
        )
        // Configuramos el mock para devolver éxito
        coEvery { apiServiceMock.getProducts() } returns Response.success(fakeApiData)

        // WHEN: Llamamos al repositorio real
        val result = repository.getAllProducts()

        // THEN: Verificamos que convirtió los datos al modelo de Dominio (formato de la UI)
        assertEquals(1, result.size)
        assertEquals("Marco Test", result[0].name)        // Verificamos mapeo nombre -> name
        assertEquals(5000, result[0].price)               // Verificamos conversión Double -> Int
        assertEquals("Grecas", result[0].category)        // Verificamos extracción de categoría
    }

    @Test
    fun `getAllProducts devuelve lista vacia cuando API falla`() = runBlocking {
        // GIVEN: La API falla con un error 500 (Internal Server Error)
        coEvery { apiServiceMock.getProducts() } returns Response.error(
            500,
            okhttp3.ResponseBody.create(null, "Error del servidor")
        )

        // WHEN: Llamamos al repositorio
        val result = repository.getAllProducts()

        // THEN: El repositorio debe capturar el error (y el Log) y devolver lista vacía
        assertTrue(result.isEmpty())
    }
}