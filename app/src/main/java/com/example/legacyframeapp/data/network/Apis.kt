package com.example.legacyframeapp.data.network

import com.example.legacyframeapp.data.network.model.*
import retrofit2.Response
import retrofit2.http.*

// ==================================================================
// 1. AUTH
// ==================================================================
interface AuthApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>
}

// ==================================================================
// 2. PRODUCTOS
// ==================================================================
interface ProductApiService {
    @GET("/api/catalog/productos")
    suspend fun getProducts(): Response<List<ProductRemote>>

    @POST("/api/catalog/productos")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductRemote>
}

// ==================================================================
// 3. PEDIDOS
// ==================================================================
interface OrderApiService {
    @POST("/api/orders")
    suspend fun createOrder(
        @Query("email") email: String,
        @Body request: OrderRequest
    ): Response<Void>

    @GET("/api/orders/my-orders")
    suspend fun getMyOrders(@Query("email") email: String): Response<List<OrderResponse>>
}

// ==================================================================
// 4. CONTACTO
// ==================================================================
interface ContactApiService {
    @POST("/api/contactos")
    suspend fun sendContact(@Body request: ContactRequest): Response<Void>
}

// ==================================================================
// 5. API EXTERNA
// ==================================================================
interface ExternalApiService {
    @GET("api")
    suspend fun getIndicadores(): Response<IndicadoresResponse>
}