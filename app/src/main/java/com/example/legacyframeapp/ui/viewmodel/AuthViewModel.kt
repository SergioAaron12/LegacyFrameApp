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
import com.example.legacyframeapp.data.network.model.CategoryIdRequest
import com.example.legacyframeapp.data.network.model.CreateProductRequest
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

// =================================================================================
// CLASES DE ESTADO (UI STATES)
// =================================================================================

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

// =================================================================================
// VIEW MODEL
// =================================================================================

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
    private val _isAdminSession = MutableStateFlow(false)

    val session = combine(userPreferences.isLoggedIn, _isAdminSession) { loggedIn, isAdmin ->
        val user = if (loggedIn) User(id = 1, nombre = "Usuario", email = "user@legacy.cl") else null
        SessionUiState(isLoggedIn = loggedIn, isAdmin = isAdmin, currentUser = user)
    }.stateIn(viewModelScope, SharingStarted.Lazily, SessionUiState())

    // --- DATOS DE LA API ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _cuadros = MutableStateFlow<List<Cuadro>>(emptyList())
    val cuadros = _cuadros.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _dolarValue = MutableStateFlow<Double?>(null)
    val dolarValue = _dolarValue.asStateFlow()

    // --- CARRITO (LOCAL) ---
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

    // --- UI STATES ---
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

    init {
        fetchCatalog()
        fetchDolarValue()
        fetchOrders()
    }

    // --- FUNCIONES DE CARGA ---

    private fun fetchCatalog() {
        viewModelScope.launch {
            try {
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
                            category = prod.category
                        )
                    }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun fetchOrders() {
        if (orderRepository == null) return
        viewModelScope.launch {
            val email = "usuario@test.com" // TODO: Usar email real de sesión
            val historial = orderRepository.getMyOrders(email)
            _orders.value = historial
        }
    }

    private fun fetchDolarValue() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.externalService.getIndicadores()
                if (response.isSuccessful) {
                    _dolarValue.value = response.body()?.dolar?.valor
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun prefetchProductImages(context: Context) { /* Opcional */ }

    // --- LOGIN ---

    fun onLoginEmailChange(v: String) { _login.update { it.copy(email = v) } }
    fun onLoginPassChange(v: String) { _login.update { it.copy(pass = v, canSubmit = v.isNotBlank()) } }

    fun submitLogin() {
        val s = _login.value
        _login.update { it.copy(isSubmitting = true, errorMsg = null) }

        viewModelScope.launch {
            val result = userRepository.login(s.email, s.pass)

            if (result.isSuccess) {
                userPreferences.setLoggedIn(true)
                if (s.email.lowercase().contains("admin")) {
                    _isAdminSession.value = true
                } else {
                    _isAdminSession.value = false
                }
                fetchOrders() // Cargar historial
            }

            _login.update { state ->
                if (result.isSuccess) state.copy(success = true, isSubmitting = false)
                else state.copy(success = false, isSubmitting = false, errorMsg = result.exceptionOrNull()?.message)
            }
        }
    }

    // --- REGISTRO (VALIDACIONES COMPLETAS) ---

    fun onRegisterNombreChange(v: String) {
        val err = validateNameLettersOnly(v)
        _register.update { s -> s.copy(nombre = v, nombreError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterApellidoChange(v: String) {
        val err = validateApellido(v)
        _register.update { s -> s.copy(apellido = v, apellidoError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterRutChange(v: String) {
        val err = validateRut(v)
        _register.update { s -> s.copy(rut = v, rutError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterDvChange(v: String) {
        val err = validateDv(v, _register.value.rut)
        _register.update { s -> s.copy(dv = v, dvError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterEmailChange(v: String) {
        val err = validateEmail(v)
        _register.update { s -> s.copy(email = v, emailError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterPhoneChange(v: String) {
        val err = validatePhoneDigitsOnly(v)
        _register.update { s -> s.copy(phone = v, phoneError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterPassChange(v: String) {
        val err = validateStrongPassword(v)
        _register.update { s -> s.copy(pass = v, passError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterConfirmChange(v: String) {
        val err = validateConfirm(_register.value.pass, v)
        _register.update { s -> s.copy(confirm = v, confirmError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }

    private fun checkRegisterCanSubmit(s: RegisterUiState): Boolean {
        return s.nombreError == null && s.apellidoError == null &&
                s.rutError == null && s.dvError == null &&
                s.emailError == null && s.phoneError == null &&
                s.passError == null && s.confirmError == null &&
                s.nombre.isNotBlank() && s.rut.isNotBlank() &&
                s.email.isNotBlank() && s.pass.isNotBlank()
    }

    fun submitRegister() {
        val s = _register.value
        _register.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val req = RegisterRequest(s.nombre, s.apellido, s.email, s.pass, s.confirm, s.rut, s.dv, s.phone)
            val res = userRepository.register(req)
            _register.update {
                if (res.isSuccess) it.copy(success = true, isSubmitting = false)
                else it.copy(success = false, isSubmitting = false, errorMsg = res.exceptionOrNull()?.message)
            }
        }
    }

    // --- CARRITO ---
    fun addProductToCart(p: Product) { viewModelScope.launch { cartRepository.addOrIncrement("product", p.id, p.name, p.price, p.imageUrl) } }
    fun addCuadroToCart(c: Cuadro) { viewModelScope.launch { cartRepository.addOrIncrement("cuadro", c.id, c.title, c.price, c.imageUrl) } }
    fun updateCartQuantity(i: CartItemEntity, q: Int) { viewModelScope.launch { cartRepository.updateQuantity(i, q) } }
    fun removeFromCart(i: CartItemEntity) { viewModelScope.launch { cartRepository.remove(i) } }
    fun clearCart() { viewModelScope.launch { cartRepository.clear() } }

    // --- PEDIDOS ---
    fun recordOrder(items: List<CartItemEntity>, total: Int) {
        if (orderRepository == null) return
        viewModelScope.launch {
            val detalles = items.map { OrderDetail(it.refId, it.name, it.quantity, it.price.toDouble()) }
            val email = "usuario@test.com"
            val res = orderRepository.createOrder(email, OrderRequest(detalles))
            if (res.isSuccess) {
                clearCart()
                fetchOrders()
            }
        }
    }

    fun showPurchaseNotification() {
        val context = getApplication<Application>().applicationContext
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        }
        val builder = NotificationCompat.Builder(context, "purchase_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Compra Exitosa!")
            .setContentText("Tu pedido ha sido registrado.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) { notify(System.currentTimeMillis().toInt(), builder.build()) }
    }

    // --- CONTACTO ---
    fun sendContactMessage(n: String, e: String, m: String, res: (Boolean) -> Unit) {
        viewModelScope.launch { res(contactRepository.sendMessage(n, e, m).isSuccess) }
    }

    // --- ADMIN (CREAR) ---
    fun onAddProductChange(name: String? = null, description: String? = null, price: String? = null) {
        _addProduct.update { it.copy(name = name ?: it.name, description = description ?: it.description, price = price ?: it.price, canSubmit = true) }
    }
    fun onImageSelected(uri: Uri?) { _addProduct.update { it.copy(imageUri = uri) } }

    fun saveProduct(ctx: Context) {
        val s = _addProduct.value
        _addProduct.update { it.copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val req = CreateProductRequest(s.name, s.description, s.price.toDoubleOrNull()?:0.0, 10, "/assets/mordura1.png", CategoryIdRequest(1))
            val ok = productRepository.createProduct(req)
            withContext(Dispatchers.Main) {
                _addProduct.update { it.copy(isSaving = false, saveSuccess = ok) }
                if (ok) fetchCatalog() else Toast.makeText(ctx, "Error al crear", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun clearAddProductState() { _addProduct.value = AddProductUiState() }

    fun onAddCuadroChange(title: String?=null, description: String?=null, price: String?=null, size: String?=null, material: String?=null, category: String?=null, artist: String?=null) {
        _addCuadro.update { it.copy(title=title?:it.title, description=description?:it.description, price=price?:it.price, size=size?:it.size, material=material?:it.material, category=category?:it.category, artist=artist?:it.artist, canSubmit=true) }
    }
    fun onCuadroImageSelected(uri: Uri?) { _addCuadro.update { it.copy(imageUri = uri) } }

    fun saveCuadro(ctx: Context) {
        val s = _addCuadro.value
        _addCuadro.update { it.copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val desc = "${s.description} | ${s.size} | ${s.material}"
            val req = CreateProductRequest(s.title, desc, s.price.toDoubleOrNull()?:0.0, 5, "/assets/mordura1.png", CategoryIdRequest(6))
            val ok = productRepository.createProduct(req)
            withContext(Dispatchers.Main) {
                _addCuadro.update { it.copy(isSaving = false, saveSuccess = ok) }
                if (ok) fetchCatalog() else Toast.makeText(ctx, "Error al crear", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun clearAddCuadroState() { _addCuadro.value = AddCuadroUiState() }

    // Otros
    fun logout() { viewModelScope.launch { userRepository.logout(); _isAdminSession.value = false } }
    fun clearLoginResult() { _login.value = LoginUiState() }
    fun clearRegisterResult() { _register.value = RegisterUiState() }
    fun createTempImageUri(): Uri {
        val context = getApplication<Application>().applicationContext
        val imageDir = File(context.cacheDir, "images").apply { mkdirs() }
        val tempFile = File.createTempFile("product_${System.currentTimeMillis()}", ".jpg", imageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }

    fun deleteProduct(p: Product) {}
    fun deleteCuadro(c: Cuadro) {}
    fun updateProductImage(ctx: Context, id: Long, uri: Uri, onDone: (Boolean, String?) -> Unit) { onDone(true, null) }

    fun onResetEmailChange(v: String) { _resetPassword.update { it.copy(email = v) } }
    fun onResetNewPassChange(v: String) { _resetPassword.update { it.copy(newPass = v) } }
    fun onResetConfirmChange(v: String) { _resetPassword.update { it.copy(confirm = v) } }
    fun submitResetPassword() {}
    fun clearResetPasswordState() { _resetPassword.value = ResetPasswordUiState() }

    fun changePassword(curr: String, new: String, res: (Boolean, String?) -> Unit) { res(true, null) }
    fun changeDisplayName(n: String, a: String?) {}

    suspend fun setThemeMode(m: String) = userPreferences.setThemeMode(m)
    suspend fun setAccentColor(c: String) = userPreferences.setAccentColor(c)
    suspend fun setFontScale(s: Float) = userPreferences.setFontScale(s)
    suspend fun setNotifOffers(b: Boolean) = userPreferences.setNotifOffers(b)
    suspend fun setNotifTracking(b: Boolean) = userPreferences.setNotifTracking(b)
    suspend fun setNotifCart(b: Boolean) = userPreferences.setNotifCart(b)
}