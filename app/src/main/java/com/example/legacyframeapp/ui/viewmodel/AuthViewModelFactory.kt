package com.example.legacyframeapp.ui.viewmodel

import androidx.lifecycle.ViewModel                              // Tipo base ViewModel
import androidx.lifecycle.ViewModelProvider                      // Factory de ViewModels
import com.example.legacyframeapp.data.repository.UserRepository   // Repositorio a inyectar
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.repository.OrderRepository

// Factory simple para crear AuthViewModel con su UserRepository.
class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository, // <--- AÑADIR ESTO
    private val cuadroRepository: CuadroRepository,
    private val cartRepository: CartRepository,
    private val userPreferences: UserPreferences,
    private val orderRepository: OrderRepository?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Pasamos ambos repositorios al constructor del ViewModel
            return AuthViewModel(
                userRepository = userRepository,
                productRepository = productRepository, // <--- AÑADIR ESTO
                cuadroRepository = cuadroRepository,
                cartRepository = cartRepository,
                userPreferences = userPreferences,
                orderRepository = orderRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}