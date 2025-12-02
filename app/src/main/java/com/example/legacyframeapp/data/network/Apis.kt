package com.example.legacyframeapp.data.network

import com.example.legacyframeapp.data.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>
}

interface ProductApiService {
    @GET("/api/catalog/productos")
    suspend fun getProducts(): Response<List<ProductRemote>>
}

interface OrderApiService {
    @POST("/api/orders")
    suspend fun createOrder(@Query("email") email: String, @Body request: OrderRequest): Response<Void>
}

interface ContactApiService {
    @POST("/api/contactos")
    suspend fun sendContact(@Body request: ContactRequest): Response<Void>
}

interface ExternalApiService {
    @GET("api") // Endpoint raíz de mindicador.cl
    suspend fun getIndicadores(): Response<IndicadoresResponse>
}