package com.example.legacyframeapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.network.RetrofitClient
import com.example.legacyframeapp.data.network.model.OrderDetail
import com.example.legacyframeapp.data.network.model.OrderRequest
import com.example.legacyframeapp.data.network.model.RegisterRequest
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.repository.ContactRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.OrderRepository
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.domain.model.Cuadro
import com.example.legacyframeapp.domain.model.Order
import com.example.legacyframeapp.domain.model.Product
import com.example.legacyframeapp.domain.model.User
import com.example.legacyframeapp.domain.validation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.NumberFormat
import java.util.Locale

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

data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val dv: String = "",
    val email: String = "",
    val phone: String = "",
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
    val isAdmin: Boolean = false,
    val currentUser: User? = null
)

data class ResetPasswordUiState(
    val email: String = "",
    val newPass: String = "",
    val confirm: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class AddProductUiState(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUri: Uri? = null,
    val nameError: String? = null,
    val priceError: String? = null,
    val imageError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMsg: String? = null
)

data class AddCuadroUiState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val size: String = "",
    val material: String = "",
    val category: String = "",
    val imageUri: Uri? = null,
    val artist: String? = null,
    val titleError: String? = null,
    val priceError: String? = null,
    val imageError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMsg: String? = null
)

class AuthViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cuadroRepository: CuadroRepository,
    private val cartRepository: CartRepository,
    private val userPreferences: UserPreferences,
    private val orderRepository: OrderRepository?,
    private val contactRepository: ContactRepository
) : AndroidViewModel(application) {

    // --- ESTADO DE SESIÓN ---
    val session = userPreferences.isLoggedIn.map { loggedIn ->
        val user = if (loggedIn) User(id = 1, nombre = "Usuario", email = "usuario@legacy.cl") else null
        SessionUiState(isLoggedIn = loggedIn, isAdmin = true, currentUser = user)
    }.stateIn(viewModelScope, SharingStarted.Lazily, SessionUiState())

    // --- CATÁLOGO ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _cuadros = MutableStateFlow<List<Cuadro>>(emptyList())
    val cuadros = _cuadros.asStateFlow()

    // --- API EXTERNA (Dólar) ---
    private val _dolarValue = MutableStateFlow<Double?>(null)
    val dolarValue = _dolarValue.asStateFlow()

    // --- CARRITO ---
    val cartItems: StateFlow<List<CartItemEntity>> = cartRepository.items()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cartTotal: StateFlow<Int> = cartRepository.total()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val cartItemCount: StateFlow<Int> = cartRepository.count()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // --- PREFERENCIAS ---
    val darkMode = userPreferences.isDarkMode.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val themeMode = userPreferences.themeMode.stateIn(viewModelScope, SharingStarted.Lazily, "system")
    val accentColor = userPreferences.accentColor.stateIn(viewModelScope, SharingStarted.Lazily, "#FF8B5C2A")
    val fontScale = userPreferences.fontScale.stateIn(viewModelScope, SharingStarted.Lazily, 1.0f)
    val notifOffers = userPreferences.notifOffers.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val notifTracking = userPreferences.notifTracking.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val notifCart = userPreferences.notifCart.stateIn(viewModelScope, SharingStarted.Lazily, true)

    // --- ESTADOS DE PANTALLAS ---
    private val _login = MutableStateFlow(LoginUiState())
    val login = _login.asStateFlow()

    private val _register = MutableStateFlow(RegisterUiState())
    val register = _register.asStateFlow()

    private val _resetPassword = MutableStateFlow(ResetPasswordUiState())
    val resetPassword = _resetPassword.asStateFlow()

    private val _addProduct = MutableStateFlow(AddProductUiState())
    val addProduct = _addProduct.asStateFlow()

    private val _addCuadro = MutableStateFlow(AddCuadroUiState())
    val addCuadro = _addCuadro.asStateFlow()

    val orders: StateFlow<List<Order>> =
        (orderRepository?.getAll() ?: flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchCatalog()
        fetchDolarValue()
    }

    // ========================================================================
    // 1. CARGA DE DATOS
    // ========================================================================

    private fun fetchCatalog() {
        // CORRECCIÓN: Eliminado Dispatchers.IO para facilitar el testing
        // Retrofit ya gestiona el hilo de fondo internamente.
        viewModelScope.launch {
            val apiList = productRepository.getAllProducts()

            _products.value = apiList.filter { !it.category.equals("cuadros", ignoreCase = true) }

            _cuadros.value = apiList.filter { it.category.equals("cuadros", ignoreCase = true) }
                .map { prod ->
                    Cuadro(
                        id = prod.id,
                        title = prod.name,
                        description = prod.description,
                        price = prod.price,
                        imageUrl = prod.imageUrl,
                        category = prod.category,
                        size = "Estándar",
                        material = "Marco de madera",
                        artist = "Legacy Frames"
                    )
                }
        }
    }

    // ========================================================================
    // 2. API EXTERNA (INDICADORES)
    // ========================================================================

    private fun fetchDolarValue() {
        // CORRECCIÓN: Eliminado Dispatchers.IO para testing
        viewModelScope.launch {
            try {
                val response = RetrofitClient.externalService.getIndicadores()
                if (response.isSuccessful && response.body() != null) {
                    val valor = response.body()!!.dolar.valor
                    _dolarValue.value = valor
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun prefetchProductImages(context: Context) { /* Opcional */ }

    // ========================================================================
    // 3. LOGIN Y REGISTRO
    // ========================================================================

    fun onLoginEmailChange(v: String) { _login.update { it.copy(email = v) } }
    fun onLoginPassChange(v: String) { _login.update { it.copy(pass = v, canSubmit = v.isNotBlank()) } }

    fun submitLogin() {
        val s = _login.value
        if (s.email.isBlank() || s.pass.isBlank()) return

        _login.update { it.copy(isSubmitting = true, errorMsg = null) }

        viewModelScope.launch {
            val result = userRepository.login(s.email, s.pass)

            // Guardamos sesión si es exitoso
            if (result.isSuccess) {
                userPreferences.setLoggedIn(true)
            }

            _login.update { state ->
                if (result.isSuccess) {
                    state.copy(success = true, isSubmitting = false)
                } else {
                    state.copy(success = false, isSubmitting = false, errorMsg = result.exceptionOrNull()?.message ?: "Error Login")
                }
            }
        }
    }

    fun submitRegister() {
        val s = _register.value
        if (s.pass != s.confirm) {
            _register.update { it.copy(confirmError = "Las contraseñas no coinciden") }; return
        }

        _register.update { it.copy(isSubmitting = true, errorMsg = null) }

        viewModelScope.launch {
            val request = RegisterRequest(
                nombre = s.nombre,
                apellido = s.apellido,
                email = s.email,
                password = s.pass,
                confirmPassword = s.confirm,
                rut = s.rut,
                dv = s.dv,
                telefono = s.phone
            )

            val result = userRepository.register(request)
            _register.update {
                if (result.isSuccess) it.copy(success = true, isSubmitting = false)
                else it.copy(success = false, isSubmitting = false, errorMsg = result.exceptionOrNull()?.message)
            }
        }
    }

    fun clearLoginResult() { _login.value = LoginUiState() }
    fun clearRegisterResult() { _register.value = RegisterUiState() }
    fun logout() { viewModelScope.launch { userRepository.logout() } }

    fun onRegisterNombreChange(v: String) { _register.update { it.copy(nombre = v) } }
    fun onRegisterApellidoChange(v: String) { _register.update { it.copy(apellido = v) } }
    fun onRegisterRutChange(v: String) { _register.update { it.copy(rut = v) } }
    fun onRegisterDvChange(v: String) { _register.update { it.copy(dv = v) } }
    fun onRegisterEmailChange(v: String) { _register.update { it.copy(email = v) } }
    fun onRegisterPhoneChange(v: String) { _register.update { it.copy(phone = v) } }
    fun onRegisterPassChange(v: String) { _register.update { it.copy(pass = v) } }
    fun onRegisterConfirmChange(v: String) { _register.update { it.copy(confirm = v) } }

    // ========================================================================
    // 4. CARRITO (LOCAL)
    // ========================================================================

    fun addProductToCart(p: Product) {
        viewModelScope.launch {
            cartRepository.addOrIncrement("product", p.id, p.name, p.price, p.imageUrl)
        }
    }

    fun addCuadroToCart(c: Cuadro) {
        viewModelScope.launch {
            cartRepository.addOrIncrement("cuadro", c.id, c.title, c.price, c.imageUrl)
        }
    }

    fun updateCartQuantity(item: CartItemEntity, qty: Int) {
        viewModelScope.launch { cartRepository.updateQuantity(item, qty) }
    }

    fun removeFromCart(item: CartItemEntity) {
        viewModelScope.launch { cartRepository.remove(item) }
    }

    fun clearCart() {
        viewModelScope.launch { cartRepository.clear() }
    }

    // ========================================================================
    // 5. PEDIDOS (API)
    // ========================================================================

    fun recordOrder(items: List<CartItemEntity>, total: Int) {
        if (orderRepository == null) return

        // CORRECCIÓN: Eliminado Dispatchers.IO para testing
        // Se asume que OrderRepository maneja el hilo con withContext
        viewModelScope.launch {
            val detalles = items.map {
                OrderDetail(
                    productoId = it.refId,
                    nombreProducto = it.name,
                    cantidad = it.quantity,
                    precioUnitario = it.price.toDouble()
                )
            }
            val request = OrderRequest(items = detalles)
            val emailUser = "usuario@test.com"

            val result = orderRepository.createOrder(emailUser, request)
            if (result.isSuccess) {
                clearCart()
            }
        }
    }

    fun showPurchaseNotification() {
        val context = getApplication<Application>().applicationContext
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val builder = NotificationCompat.Builder(context, "purchase_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Compra Exitosa!")
            .setContentText("Tu pedido ha sido registrado correctamente.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    // ========================================================================
    // 6. CONTACTO (API)
    // ========================================================================

    fun sendContactMessage(nombre: String, email: String, mensaje: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = contactRepository.sendMessage(nombre, email, mensaje)
            onResult(result.isSuccess)
        }
    }

    // ========================================================================
    // 7. ADMIN Y OTROS
    // ========================================================================

    fun onAddProductChange(name: String = "", description: String = "", price: String = "") {
        _addProduct.update { it.copy(name = name, description = description, price = price, canSubmit = true) }
    }
    fun onImageSelected(uri: Uri?) { _addProduct.update { it.copy(imageUri = uri) } }
    fun saveProduct(context: Context) {
        Toast.makeText(context, "API POST no implementada", Toast.LENGTH_SHORT).show()
        _addProduct.update { it.copy(saveSuccess = true) }
    }
    fun clearAddProductState() { _addProduct.value = AddProductUiState() }

    fun onAddCuadroChange(title: String="", description: String="", price: String="", size: String="", material: String="", category: String="", artist: String?="") {
        _addCuadro.update { it.copy(title=title, description=description, price=price, canSubmit=true) }
    }
    fun onCuadroImageSelected(uri: Uri?) { _addCuadro.update { it.copy(imageUri = uri) } }
    fun saveCuadro(context: Context) {
        Toast.makeText(context, "API POST no implementada", Toast.LENGTH_SHORT).show()
        _addCuadro.update { it.copy(saveSuccess = true) }
    }
    fun clearAddCuadroState() { _addCuadro.value = AddCuadroUiState() }

    fun deleteProduct(p: Product) { /* TODO: API DELETE */ }
    fun deleteCuadro(c: Cuadro) { /* TODO: API DELETE */ }

    fun updateProductImage(ctx: Context, id: Long, uri: Uri, onDone: (Boolean, String?) -> Unit) { onDone(true, null) }

    fun onResetEmailChange(v: String) { _resetPassword.update { it.copy(email = v) } }
    fun onResetNewPassChange(v: String) { _resetPassword.update { it.copy(newPass = v) } }
    fun onResetConfirmChange(v: String) { _resetPassword.update { it.copy(confirm = v) } }
    fun submitResetPassword() { /* Placeholder */ }
    fun clearResetPasswordState() { _resetPassword.value = ResetPasswordUiState() }

    fun changePassword(curr: String, new: String, res: (Boolean, String?) -> Unit) { res(true, null) }
    fun changeDisplayName(n: String, a: String?) { }

    suspend fun setThemeMode(m: String) = userPreferences.setThemeMode(m)
    suspend fun setAccentColor(c: String) = userPreferences.setAccentColor(c)
    suspend fun setFontScale(s: Float) = userPreferences.setFontScale(s)
    suspend fun setNotifOffers(b: Boolean) = userPreferences.setNotifOffers(b)
    suspend fun setNotifTracking(b: Boolean) = userPreferences.setNotifTracking(b)
    suspend fun setNotifCart(b: Boolean) = userPreferences.setNotifCart(b)

    // --- CÁMARA ---
    fun createTempImageUri(): Uri {
        val context = getApplication<Application>().applicationContext
        val imageDir = File(context.cacheDir, "images").apply { mkdirs() }
        val tempFile = File.createTempFile("product_${System.currentTimeMillis()}", ".jpg", imageDir)
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, tempFile)
    }
}