package com.example.legacyframeapp.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Cambia esto según tu entorno (Emulador vs Celular físico)
    // Emulador: "http://10.0.2.2:8083" (para productos), pero tenemos varios puertos...
    // OJO: Aquí hay un tema. Tienes microservicios en puertos distintos (8083, 8085, etc).
    // Si RetrofitClient es único, solo apunta a UNA base url.
    // Para simplificar tu presentación, asumiremos que usas URLs completas en los repositorios
    // o que usas un Gateway.

    // Si NO usas Gateway, Retrofit necesita crear servicios distintos para cada puerto.
    // Pero para arreglar el 403 del Auth (Puerto 8085), hagamos esto:

    private const val AUTH_BASE_URL = "http://10.0.2.2:8085" // Puerto de Auth
    private const val PRODUCT_BASE_URL = "http://10.0.2.2:8083" // Puerto de Productos
    private const val PEDIDOS_BASE_URL = "http://10.0.2.2:8084" // Puerto de Pedidos
    private const val CONTACTO_BASE_URL = "http://10.0.2.2:8081" // Puerto de Contacto

    // --- INTERCEPTOR DE TOKEN (El arreglo del 403) ---
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Si tenemos token, lo agregamos
            TokenManager.token?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            chain.proceed(requestBuilder.build())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder().setLenient().create()

    // --- CREADOR DE SERVICIOS ---
    // Función auxiliar para crear servicios en distintos puertos
    private fun <T> createService(baseUrl: String, serviceClass: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client) // ¡Aquí va el cliente con el token!
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(serviceClass)
    }

    // --- TUS SERVICIOS ---
    val authService: AuthApiService by lazy { createService(AUTH_BASE_URL, AuthApiService::class.java) }
    val productService: ProductApiService by lazy { createService(PRODUCT_BASE_URL, ProductApiService::class.java) }
    val orderService: OrderApiService by lazy { createService(PEDIDOS_BASE_URL, OrderApiService::class.java) }
    val contactService: ContactApiService by lazy { createService(CONTACTO_BASE_URL, ContactApiService::class.java) }

    // Api Externa
    val externalService: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}