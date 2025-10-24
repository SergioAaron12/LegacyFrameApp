package com.example.legacyframeapp.ui.viewmodel // Asegúrate que el paquete sea correcto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.legacyframeapp.domain.validation.* // Tus funciones de validación
import com.example.legacyframeapp.data.repository.UserRepository // Importa el repositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// --- AÑADIR IMPORTS ---
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.data.local.user.UserEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.example.legacyframeapp.domain.validation.validateNameLettersOnly // Usaremos este validador
import com.example.legacyframeapp.domain.validation.validatePhoneDigitsOnly // Lo usaremos para el precio

// --- ESTADOS DE UI ---

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// --- DEFINICIÓN COMPLETA Y CORRECTA DE RegisterUiState ---
data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "", // Opcional en UI, pero presente en estado
    val rut: String = "",
    val dv: String = "",
    val email: String = "",
    val phone: String = "", // Sigue String para UI, pero validado como numérico
    val pass: String = "",
    val confirm: String = "",
    val nombreError: String? = null,
    val apellidoError: String? = null,
    val rutError: String? = null,
    val dvError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class SessionUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: UserEntity? = null,
    val isAdmin: Boolean = false
)

data class AddProductUiState(
    val name: String = "",
    val description: String = "",
    val price: String = "",       // Usamos String para el TextField
    val imageUri: String = "",  // Por ahora guardaremos el URI como string

    // Estado del formulario
    val nameError: String? = null,
    val priceError: String? = null,
    val imageError: String? = null, // Para cuando implementemos la cámara

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMsg: String? = null
)
// -----------------------------------------------------------

class AuthViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    // Flujos de estado
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _session = MutableStateFlow(SessionUiState())
    val session: StateFlow<SessionUiState> = _session

    val products: StateFlow<List<ProductEntity>> =
        productRepository.getAllProducts()
            .stateIn(
                scope = viewModelScope,
                // Empezar a recolectar cuando la UI esté visible
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList() // Empezar con lista vacía
            )

    private val _addProduct = MutableStateFlow(AddProductUiState())
    val addProduct: StateFlow<AddProductUiState> = _addProduct

    // --- Funciones on...Change para Login (Sin cambios) ---
    fun onLoginEmailChange(email: String) {
        val emailError = validateEmail(email)
        _login.update {
            it.copy(
                email = email,
                emailError = emailError,
                canSubmit = emailError == null && it.pass.isNotBlank() && it.passError == null
            )
        }
    }
    fun onLoginPassChange(pass: String) {
        val passError = if (pass.isBlank()) "La contraseña es obligatoria" else null
        _login.update {
            it.copy(
                pass = pass,
                passError = passError,
                canSubmit = passError == null && it.email.isNotBlank() && it.emailError == null
            )
        }
    }

    // --- Funciones on...Change para Registro (Completas) ---
    fun onRegisterNombreChange(nombre: String) {
        val nombreError = validateNameLettersOnly(nombre)
        _register.update { s ->
            s.copy(
                nombre = nombre,
                nombreError = nombreError,
                canSubmit = checkRegisterCanSubmit(s.copy(nombre = nombre, nombreError = nombreError))
            )
        }
    }

    fun onRegisterApellidoChange(apellido: String) {
        val apellidoError = validateApellido(apellido) // Usa tu validador (solo letras si no vacío)
        _register.update { s ->
            s.copy(
                apellido = apellido,
                apellidoError = apellidoError,
                canSubmit = checkRegisterCanSubmit(s.copy(apellido = apellido, apellidoError = apellidoError))
            )
        }
    }

    fun onRegisterRutChange(rut: String) {
        val rutError = validateRut(rut) // Usa tu validador
        // Al cambiar el RUT, revalidamos el DV
        val dvError = validateDv(_register.value.dv, rut)
        _register.update { s ->
            s.copy(
                rut = rut,
                rutError = rutError,
                dvError = dvError, // Actualiza error de DV también
                canSubmit = checkRegisterCanSubmit(s.copy(rut = rut, rutError = rutError, dvError = dvError))
            )
        }
    }

    fun onRegisterDvChange(dv: String) {
        // Valida DV contra el RUT actual en el estado
        val dvError = validateDv(dv, _register.value.rut) // Usa tu validador
        _register.update { s ->
            s.copy(
                dv = dv.uppercase(), // Guarda siempre en mayúscula
                dvError = dvError,
                canSubmit = checkRegisterCanSubmit(s.copy(dv = dv.uppercase(), dvError = dvError))
            )
        }
    }

    fun onRegisterEmailChange(email: String) {
        val emailError = validateEmail(email)
        _register.update { s ->
            s.copy(
                email = email,
                emailError = emailError,
                canSubmit = checkRegisterCanSubmit(s.copy(email = email, emailError = emailError))
            )
        }
    }
    fun onRegisterPhoneChange(phone: String) {
        val phoneError = validatePhoneDigitsOnly(phone) // Asegura que sea numérico y no vacío
        _register.update { s ->
            s.copy(
                phone = phone,
                phoneError = phoneError,
                canSubmit = checkRegisterCanSubmit(s.copy(phone = phone, phoneError = phoneError))
            )
        }
    }
    fun onRegisterPassChange(pass: String) {
        val passError = validateStrongPassword(pass)
        // Revalida la confirmación si la contraseña cambia
        val confirmError = validateConfirm(pass, _register.value.confirm)
        _register.update { s ->
            s.copy(
                pass = pass,
                passError = passError,
                confirmError = confirmError, // Actualiza error de confirmación también
                canSubmit = checkRegisterCanSubmit(s.copy(pass = pass, passError = passError, confirmError = confirmError))
            )
        }
    }
    fun onRegisterConfirmChange(confirm: String) {
        val confirmError = validateConfirm(_register.value.pass, confirm)
        _register.update { s ->
            s.copy(
                confirm = confirm,
                confirmError = confirmError,
                canSubmit = checkRegisterCanSubmit(s.copy(confirm = confirm, confirmError = confirmError))
            )
        }
    }

    // --- checkRegisterCanSubmit (Completo) ---
    private fun checkRegisterCanSubmit(s: RegisterUiState): Boolean {
        // Comprueba todos los campos obligatorios y sus errores
        // ApellidoError se incluye porque si el usuario escribe algo inválido, no se debe poder enviar
        return s.nombreError == null && s.apellidoError == null &&
                s.rutError == null && s.dvError == null &&
                s.emailError == null && s.phoneError == null &&
                s.passError == null && s.confirmError == null &&
                // Verifica que los campos obligatorios no estén vacíos
                s.nombre.isNotBlank() && s.rut.isNotBlank() && s.dv.isNotBlank() &&
                s.email.isNotBlank() && s.phone.isNotBlank() && // Phone es obligatorio
                s.pass.isNotBlank() && s.confirm.isNotBlank()
    }

    fun onAddProductChange(
        name: String = _addProduct.value.name,
        description: String = _addProduct.value.description,
        price: String = _addProduct.value.price
    ) {
        // Validaciones en tiempo real
        val nameError = if(name.isBlank()) "El nombre es obligatorio" else null
        val priceError = validatePhoneDigitsOnly(price) // Re-usamos el validador de teléfono

        _addProduct.update {
            it.copy(
                name = name,
                description = description,
                price = price,
                nameError = nameError,
                priceError = priceError,
                canSubmit = nameError == null && priceError == null && !it.isSaving
                // (Añadiremos la validación de imagen aquí más tarde)
            )
        }
    }

    // Función para guardar el producto
    fun saveProduct() {
        val s = _addProduct.value

        // Doble chequeo por si acaso
        if (!s.canSubmit || s.isSaving) return

        // --- TODO: Validación de Imagen ---
        // if (s.imageUri.isBlank()) {
        //     _addProduct.update { it.copy(imageError = "Debe seleccionar una imagen") }
        //     return
        // }
        // ------------------------------------

        _addProduct.update { it.copy(isSaving = true, errorMsg = null) }

        viewModelScope.launch {
            try {
                // Convertimos el precio (que es String) a Int
                val priceInt = s.price.toInt()

                // --- TODO: Procesamiento de Imagen ---
                // Aquí es donde copiaremos la imagen de s.imageUri al almacenamiento
                // interno y obtendremos el "imagePath" real.
                // Por AHORA, usaremos un placeholder.
                val finalImagePath = "" // <-- Placeholder
                // ------------------------------------

                val newProduct = ProductEntity(
                    name = s.name.trim(),
                    description = s.description.trim(),
                    price = priceInt,
                    imagePath = finalImagePath // Usamos el placeholder
                )

                // Insertamos en la base de datos
                productRepository.insert(newProduct)

                // Éxito
                _addProduct.update { it.copy(isSaving = false, saveSuccess = true) }

            } catch (e: NumberFormatException) {
                // Error si el precio no es un número (aunque el validador debería pararlo)
                _addProduct.update { it.copy(isSaving = false, errorMsg = "El precio ingresado no es válido.") }
            } catch (e: Exception) {
                // Cualquier otro error (ej: guardando la imagen, error de DB)
                _addProduct.update { it.copy(isSaving = false, errorMsg = e.message ?: "Error desconocido al guardar") }
            }
        }
    }

    // Función para resetear el formulario después de guardar
    fun clearAddProductState() {
        _addProduct.update { AddProductUiState() } // Resetea al estado inicial
    }
    // ------------------------------------

    // --- Login con Repositorio (Sin cambios) ---
    fun submitLogin() {
        val s = _login.value

        // (Tus validaciones de campos van aquí...)
        if (s.emailError != null || s.passError != null) {
            _login.update { it.copy(isSubmitting = false) }
            return
        }

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = userRepository.login(s.email, s.pass)

            _login.update {
                if (result.isSuccess) {
                    val user = result.getOrNull() // <--- Obtenemos el usuario

                    // --- AÑADIR ESTO ---
                    // Actualizamos el estado de la sesión global
                    _session.update { sessionState ->
                        sessionState.copy(
                            isLoggedIn = true,
                            currentUser = user,
                            isAdmin = user?.rolId == 1 // 1 es ADMIN_ROL_ID
                        )
                    }
                    // ---------------------

                    it.copy(isSubmitting = false, success = true, errorMsg = null) // OK
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Credenciales inválidas") // Error
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // (Más adelante conectaremos esto a UserPreferences.kt)
            _session.update { SessionUiState() } // Resetea al estado inicial (loggedOut)
            // (Opcional) Limpiar los campos de login
            _login.update { LoginUiState() }
        }
    }

    // --- submitRegister (Completo) ---
    fun submitRegister() {
        val s = _register.value
        val canSubmitFinal = checkRegisterCanSubmit(s)
        if (!canSubmitFinal) {
            // Actualiza todos los errores para mostrarlos si están vacíos al intentar enviar
            _register.update { it.copy(
                nombreError = it.nombreError ?: if(it.nombre.isBlank()) "El nombre es obligatorio" else null,
                apellidoError = it.apellidoError, // No es obligatorio, solo muestra si la validación falló
                rutError = it.rutError ?: if(it.rut.isBlank()) "El RUT es obligatorio" else null,
                dvError = it.dvError ?: if(it.dv.isBlank()) "El DV es obligatorio" else validateDv(it.dv, it.rut), // Revalida DV
                emailError = it.emailError ?: if(it.email.isBlank()) "El email es obligatorio" else null,
                phoneError = it.phoneError ?: if(it.phone.isBlank()) "El teléfono es obligatorio" else null,
                passError = it.passError ?: if(it.pass.isBlank()) "La contraseña es obligatoria" else null,
                confirmError = it.confirmError ?: if(it.confirm.isBlank()) "Confirma la contraseña" else if (it.pass != it.confirm) "Las contraseñas no coinciden" else null
            )}
            return
        }
        if (s.isSubmitting) return

        // Convierte teléfono a Int
        val phoneInt: Int
        try {
            phoneInt = s.phone.toInt()
        } catch (e: NumberFormatException) {
            _register.update { it.copy(isSubmitting = false, errorMsg = "Error interno: El teléfono no es un número válido.") }
            return
        }

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            // Llama al repositorio con todos los parámetros
            val result = userRepository.register(
                nombre = s.nombre,
                apellido = s.apellido.ifBlank { null }, // Envía null si apellido está vacío
                rut = s.rut,
                dv = s.dv,
                email = s.email,
                password = s.pass,
                phone = phoneInt // Pasa el Int
            )

            // Interpreta resultado
            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null) // OK
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar") // Error
                }
            }
        }
    }

    // Funciones para limpiar flags (Sin cambios)
    fun clearLoginResult() { _login.update { it.copy(success = false, errorMsg = null) } }
    fun clearRegisterResult() { _register.update { it.copy(success = false, errorMsg = null) } }
}

