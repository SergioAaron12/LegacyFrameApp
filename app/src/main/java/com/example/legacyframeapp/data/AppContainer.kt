package com.example.legacyframeapp.data

import android.app.Application
import com.example.legacyframeapp.data.local.database.AppDatabase
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.remote.RetrofitClient
import com.example.legacyframeapp.data.repository.*
import com.example.legacyframeapp.domain.repository.CuadroRepository
import com.example.legacyframeapp.domain.repository.ProductRepository
import com.example.legacyframeapp.ui.viewmodel.AuthViewModelFactory

class AppContainer(private val application: Application) {

    private val db by lazy {
        AppDatabase.getDatabase(application)
    }

    // Servicios API
    private val authApiService by lazy {
        RetrofitClient.authApiService
    }

    private val productApiService by lazy {
        RetrofitClient.productApiService
    }

    private val orderApiService by lazy {
        RetrofitClient.orderApiService
    }

    private val contactApiService by lazy {
        RetrofitClient.contactApiService
    }

    private val userPreferences: UserPreferences by lazy {
        UserPreferences(application)
    }

    private val userRepository: UserRepository by lazy {
        UserRepository(userPreferences)
    }

    private val productRepository: ProductRepository by lazy {
        ProductRepository(productApiService)
    }

    private val cuadroRepository: CuadroRepository by lazy {
        CuadroRepositoryImpl(productApiService) // Asumiendo que cuadros son productos
    }

    private val cartRepository: CartRepository by lazy {
        CartRepository(db.cartDao())
    }

    private val orderRepository: OrderRepository by lazy {
        OrderRepository(orderApiService)
    }

    private val contactRepository: ContactRepository by lazy {
        ContactRepository(contactApiService)
    }

    val authViewModelFactory: AuthViewModelFactory by lazy {
        AuthViewModelFactory(
            application,
            userRepository,
            productRepository,
            cuadroRepository,
            cartRepository,
            userPreferences,
            orderRepository,
            contactRepository
        )
    }
}
