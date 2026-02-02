package com.example.legacyframeapp.domain.repository

import com.example.legacyframeapp.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}
