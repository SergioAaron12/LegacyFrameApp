package com.example.legacyframeapp.data.network

import com.example.legacyframeapp.data.network.model.*
import retrofit2.Response
import retrofit2.http.*

// ==================================================================
// 1. MICROSERVICIO DE AUTENTICACIÓN (Puerto 8085)
// ==================================================================

interface AuthApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    // Endpoint para obtener perfil si lo necesitas en el futuro
    // @GET("/auth/perfil")
    // suspend fun getProfile(@Query("email") email: String): Response<UserProfileDto>
}

// ==================================================================
// 2. MICROSERVICIO DE PRODUCTOS (Puerto 8083)
// ==================================================================

interface ProductApiService {
    @GET("/api/catalog/productos")
    // IMPORTANTE: Devuelve ProductRemote (el modelo del backend)
    // El repositorio se encargará de convertirlo a Product (el modelo de la UI)
    suspend fun getProducts(): Response<List<ProductRemote>>

    // Si tuvieras un endpoint para filtrar por categoría en el servidor:
    // @GET("/api/catalog/productos")
    // suspend fun getProductsByCategory(@Query("categoria") categoria: String): Response<List<ProductRemote>>
}

// ==================================================================
// 3. MICROSERVICIO DE PEDIDOS (Puerto 8084)
// ==================================================================

interface OrderApiService {
    @POST("/api/orders")
    suspend fun createOrder(
        @Query("email") email: String,
        @Body request: OrderRequest
    ): Response<Void> // Asumimos que devuelve 200 OK sin cuerpo o ignoramos la respuesta

    // Para el historial de compras
    // @GET("/api/orders/my-orders")
    // suspend fun getMyOrders(@Query("email") email: String): Response<List<OrderRemote>>
}

// ==================================================================
// 4. MICROSERVICIO DE CONTACTO (Puerto 8081)
// ==================================================================

interface ContactApiService {
    @POST("/api/contactos")
    suspend fun sendContact(@Body request: ContactRequest): Response<Void>
}