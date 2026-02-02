package com.example.legacyframeapp.data.remote

import com.example.legacyframeapp.data.network.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URLs base para cada microservicio
    private const val AUTH_BASE_URL = "http://10.0.2.2:8085/"
    private const val PRODUCT_BASE_URL = "http://10.0.2.2:8083/"
    private const val ORDER_BASE_URL = "http://10.0.2.2:8084/"
    private const val CONTACT_BASE_URL = "http://10.0.2.2:8081/"
    private const val EXTERNAL_BASE_URL = "https://mindicador.cl/" // Para indicadores económicos

    // Instancias para Auth
    val authApiService: AuthApiService by lazy {
        createRetrofit(AUTH_BASE_URL).create(AuthApiService::class.java)
    }

    // Alias para compatibilidad
    val authService: AuthApiService by lazy { authApiService }

    // Instancias para Productos
    val productApiService: ProductApiService by lazy {
        createRetrofit(PRODUCT_BASE_URL).create(ProductApiService::class.java)
    }

    val productService: ProductApiService by lazy { productApiService }

    // Instancias para Pedidos
    val orderApiService: OrderApiService by lazy {
        createRetrofit(ORDER_BASE_URL).create(OrderApiService::class.java)
    }

    val orderService: OrderApiService by lazy { orderApiService }

    // Instancias para Contacto
    val contactApiService: ContactApiService by lazy {
        createRetrofit(CONTACT_BASE_URL).create(ContactApiService::class.java)
    }

    val contactService: ContactApiService by lazy { contactApiService }

    // Instancia para API externa
    val externalService: ExternalApiService by lazy {
        createRetrofit(EXTERNAL_BASE_URL).create(ExternalApiService::class.java)
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
