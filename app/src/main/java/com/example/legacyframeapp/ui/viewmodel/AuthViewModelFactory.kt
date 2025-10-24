package com.example.legacyframeapp.ui.viewmodel
import android.app.Application
import androidx.lifecycle.ViewModel                              // Tipo base ViewModel
import androidx.lifecycle.ViewModelProvider                      // Factory de ViewModels
import com.example.legacyframeapp.data.repository.UserRepository   // Repositorio a inyectar
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.CartRepository

// Factory simple para crear AuthViewModel con su UserRepository.
class AuthViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Pasamos ambos repositorios al constructor del ViewModel
            return AuthViewModel(
                application = application,
                userRepository = userRepository,
                productRepository = productRepository,
                cartRepository = cartRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}