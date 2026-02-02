package com.example.legacyframeapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.repository.*
import com.example.legacyframeapp.domain.repository.CuadroRepository
import com.example.legacyframeapp.domain.repository.ProductRepository

class AuthViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cuadroRepository: CuadroRepository,
    private val cartRepository: CartRepository,
    private val userPreferences: UserPreferences,
    private val orderRepository: OrderRepository?,
    private val contactRepository: ContactRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                application,
                userRepository,
                productRepository,
                cuadroRepository,
                cartRepository,
                userPreferences,
                orderRepository,
                contactRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
