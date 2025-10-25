package com.example.legacyframeapp.ui.viewmodel

import android.app.Application // <-- 1. AÑADIR IMPORT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.CartRepository

class AuthViewModelFactory(
    private val application: Application, // <-- 2. AÑADIR AL CONSTRUCTOR
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cuadroRepository: CuadroRepository,
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Pasamos los 4 repositorios Y la aplicación
            return AuthViewModel(
                application = application, // <-- 3. PASARLO AQUÍ
                userRepository = userRepository,
                productRepository = productRepository,
                cuadroRepository = cuadroRepository,
                cartRepository = cartRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}