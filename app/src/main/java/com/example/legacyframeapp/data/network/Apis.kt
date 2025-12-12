package com.example.legacyframeapp.data.network

import com.example.legacyframeapp.data.network.model.*
import retrofit2.Response
import retrofit2.http.*

// ==================================================================
// 1. AUTH (LOGIN, REGISTRO Y PERFIL)
// ==================================================================
interface AuthApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    // --- NUEVOS ENDPOINTS DE PERFIL ---
    @GET("/auth/perfil")
    suspend fun getProfile(@Query("email") email: String): Response<UserProfileResponse>

    @PUT("/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>
}

// ==================================================================
// 2. PRODUCTOS (CATÁLOGO Y GESTIÓN)
// ==================================================================
interface ProductApiService {
    @GET("/api/catalog/productos")
    suspend fun getProducts(): Response<List<ProductRemote>>

    @POST("/api/catalog/productos")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductRemote>

    @DELETE("/api/catalog/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Void>
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