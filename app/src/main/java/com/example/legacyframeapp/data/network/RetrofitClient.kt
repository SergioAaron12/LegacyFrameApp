package com.example.legacyframeapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 es el "localhost" desde el emulador de Android
    private const val BASE_URL_AUTH = "http://10.0.2.2:8085/"
    private const val BASE_URL_PRODUCTOS = "http://10.0.2.2:8083/"
    private const val BASE_URL_PEDIDOS = "http://10.0.2.2:8084/"

    // Cliente Genérico
    private fun <T> buildService(baseUrl: String, service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }

    // Tus Servicios
    val authService: AuthApiService by lazy { buildService(BASE_URL_AUTH, AuthApiService::class.java) }
    val productService: ProductApiService by lazy { buildService(BASE_URL_PRODUCTOS, ProductApiService::class.java) }
    val orderService: OrderApiService by lazy { buildService(BASE_URL_PEDIDOS, OrderApiService::class.java) }
}