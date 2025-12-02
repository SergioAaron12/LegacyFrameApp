package com.example.legacyframeapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URLs Internas (Microservicios)
    private const val BASE_URL_AUTH = "http://10.0.2.2:8085/"
    private const val BASE_URL_PRODUCTOS = "http://10.0.2.2:8083/"
    private const val BASE_URL_PEDIDOS = "http://10.0.2.2:8084/"
    private const val BASE_URL_CONTACTO = "http://10.0.2.2:8081/"

    // URL Externa (Internet)
    private const val BASE_URL_EXTERNAL = "https://mindicador.cl/"

    // Constructor Genérico
    private fun <T> buildService(baseUrl: String, serviceClass: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    // --- Servicios Internos ---
    val authService: AuthApiService by lazy { buildService(BASE_URL_AUTH, AuthApiService::class.java) }
    val productService: ProductApiService by lazy { buildService(BASE_URL_PRODUCTOS, ProductApiService::class.java) }
    val orderService: OrderApiService by lazy { buildService(BASE_URL_PEDIDOS, OrderApiService::class.java) }
    val contactService: ContactApiService by lazy { buildService(BASE_URL_CONTACTO, ContactApiService::class.java) }

    // --- Servicio Externo (Dolar) ---
    val externalService: ExternalApiService by lazy {
        buildService(BASE_URL_EXTERNAL, ExternalApiService::class.java)
    }
}