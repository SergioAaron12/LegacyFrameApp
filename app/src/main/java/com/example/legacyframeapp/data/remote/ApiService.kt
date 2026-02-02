package com.example.legacyframeapp.data.remote

import com.example.legacyframeapp.data.remote.dto.CuadroDto
import com.example.legacyframeapp.data.remote.dto.ProductDto
import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("cuadros")
    suspend fun getCuadros(): List<CuadroDto>
}
