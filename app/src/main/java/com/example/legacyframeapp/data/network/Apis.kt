package com.example.legacyframeapp.data.network

import com.example.legacyframeapp.data.network.model.* // Importa tus modelos de red
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
    // IMPORTANTE: Debe devolver List<ProductRemote>
    suspend fun getProducts(): Response<List<ProductRemote>>
}

interface OrderApiService {
    @POST("/api/orders")
    suspend fun createOrder(@Query("email") email: String, @Body request: OrderRequest): Response<Void>
}