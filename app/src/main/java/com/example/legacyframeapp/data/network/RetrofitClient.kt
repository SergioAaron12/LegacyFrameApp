package com.example.legacyframeapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URLs base para cada microservicio (10.0.2.2 es localhost desde el emulador)
    private const val BASE_URL_AUTH = "http://10.0.2.2:8085/"
    private const val BASE_URL_PRODUCTOS = "http://10.0.2.2:8083/"
    private const val BASE_URL_PEDIDOS = "http://10.0.2.2:8084/"
    private const val BASE_URL_CONTACTO = "http://10.0.2.2:8081/"

    // Función genérica para construir el servicio Retrofit
    private fun <T> buildService(baseUrl: String, serviceClass: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    // 1. Servicio de Autenticación (Login, Registro)
    val authService: AuthApiService by lazy {
        buildService(BASE_URL_AUTH, AuthApiService::class.java)
    }

    // 2. Servicio de Productos (Catálogo de Molduras y Cuadros)
    val productService: ProductApiService by lazy {
        buildService(BASE_URL_PRODUCTOS, ProductApiService::class.java)
    }

    // 3. Servicio de Pedidos (Crear orden, Historial)
    val orderService: OrderApiService by lazy {
        buildService(BASE_URL_PEDIDOS, OrderApiService::class.java)
    }

    // 4. Servicio de Contacto (Enviar mensajes)
    val contactService: ContactApiService by lazy {
        buildService(BASE_URL_CONTACTO, ContactApiService::class.java)
    }
}