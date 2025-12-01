package com.example.legacyframeapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.repository.ContactRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.OrderRepository
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.UserRepository

class AuthViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cuadroRepository: CuadroRepository,
    private val cartRepository: CartRepository,
    private val userPreferences: UserPreferences,
    private val orderRepository: OrderRepository?,
    private val contactRepository: ContactRepository // <--- ESTO FALTABA
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Ahora sí podemos pasar todos los ingredientes
            return AuthViewModel(
                application = application,
                userRepository = userRepository,
                productRepository = productRepository,
                cuadroRepository = cuadroRepository,
                cartRepository = cartRepository,
                userPreferences = userPreferences,
                orderRepository = orderRepository,
                contactRepository = contactRepository // <--- Y AQUÍ TAMBIÉN
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}