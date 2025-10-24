package com.example.legacyframeapp.ui.viewmodel

import androidx.lifecycle.ViewModel                              // Tipo base ViewModel
import androidx.lifecycle.ViewModelProvider                      // Factory de ViewModels
import com.example.legacyframeapp.data.repository.UserRepository   // Repositorio a inyectar
import com.example.legacyframeapp.data.repository.ProductRepository

// Factory simple para crear AuthViewModel con su UserRepository.
class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository // <--- AÑADIR ESTO
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Pasamos ambos repositorios al constructor del ViewModel
            return AuthViewModel(
                userRepository = userRepository,
                productRepository = productRepository // <--- AÑADIR ESTO
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}