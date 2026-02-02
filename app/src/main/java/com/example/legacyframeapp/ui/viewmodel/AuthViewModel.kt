package com.example.legacyframeapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.network.TokenManager
import com.example.legacyframeapp.data.network.model.*
import com.example.legacyframeapp.data.remote.RetrofitClient
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.repository.ContactRepository
import com.example.legacyframeapp.data.repository.OrderRepository
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.domain.model.Cuadro
import com.example.legacyframeapp.domain.model.Order
import com.example.legacyframeapp.domain.model.Product
import com.example.legacyframeapp.domain.model.User
import com.example.legacyframeapp.domain.repository.CuadroRepository
import com.example.legacyframeapp.domain.repository.ProductRepository
import com.example.legacyframeapp.domain.validation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

// --- ESTADO DEL PERFIL ---
data class ProfileUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val updateSuccess: Boolean = false,
    val errorMsg: String? = null
)

data class AddProductUiState(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val categoryId: Long = 2, // Por defecto Grecas (ID 2)
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

    // ⚠️ CONFIGURACIÓN DE IDs (Ajustar según tu base de datos)
    private val ID_CATEGORIA_CUADROS = 1L
    private val ID_DEFECTO_MOLDURAS  = 2L

    // --- SESIÓN ---
    private val _isAdminSession = MutableStateFlow(false)

    val session = combine(userPreferences.isLoggedIn, _isAdminSession) { loggedIn, isAdmin ->
        val user = if (loggedIn) User(id = "1", nombre = "Usuario", email = "user@legacy.cl") else null
        SessionUiState(isLoggedIn = loggedIn, isAdmin = isAdmin, currentUser = user)
    }.stateIn(viewModelScope, SharingStarted.Lazily, SessionUiState())

    // --- DATOS ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _cuadros = MutableStateFlow<List<Cuadro>>(emptyList())
    val cuadros = _cuadros.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

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

    // --- UI STATES ---
    private val _login = MutableStateFlow(LoginUiState())
    val login = _login.asStateFlow()

    private val _register = MutableStateFlow(RegisterUiState())
    val register = _register.asStateFlow()

    private val _resetPassword = MutableStateFlow(ResetPasswordUiState())
    val resetPassword = _resetPassword.asStateFlow()

    private val _addProduct = MutableStateFlow(AddProductUiState(categoryId = ID_DEFECTO_MOLDURAS))
    val addProduct = _addProduct.asStateFlow()

    private val _addCuadro = MutableStateFlow(AddCuadroUiState())
    val addCuadro = _addCuadro.asStateFlow()

    private val _profile = MutableStateFlow(ProfileUiState())
    val profile = _profile.asStateFlow()

    init {
        // Sincronizar Token
        viewModelScope.launch {
            userPreferences.authToken.collect { tokenGuardado ->
                TokenManager.token = tokenGuardado
            }
        }

        fetchCatalog()
        fetchDolarValue()
        fetchOrders()
    }

    // --- FUNCIONES DE CARGA ---

    private fun fetchCatalog() {
        viewModelScope.launch {
            try {
                _products.value = productRepository.getProducts()
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar error de productos
            }
            try {
                _cuadros.value = cuadroRepository.getCuadros()
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar error de cuadros
            }
        }
    }

    // --- CORRECCIÓN 1: Obtener pedidos con el email REAL ---
    private fun fetchOrders() {
        if (orderRepository == null) return
        viewModelScope.launch {
            // Obtenemos el email guardado
            val email = userPreferences.getEmail()

            if (!email.isNullOrBlank()) {
                val historial = orderRepository.getMyOrders(email)
                _orders.value = historial
            } else {
                _orders.value = emptyList()
            }
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
                // Verificar si es admin
                if (s.email.lowercase().contains("admin")) {
                    _isAdminSession.value = true
                } else {
                    _isAdminSession.value = false
                }

                // Recargar datos ahora que tenemos el usuario real
                fetchOrders()
                loadUserProfile()
            }

            _login.update { state ->
                if (result.isSuccess) state.copy(success = true, isSubmitting = false)
                else state.copy(success = false, isSubmitting = false, errorMsg = result.exceptionOrNull()?.message)
            }
        }
    }

    // --- REGISTRO ---

    fun onRegisterNombreChange(v: String) {
        val err = validateNameLettersOnly(v)
        _register.update { s -> s.copy(nombre = v, nombreError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterApellidoChange(v: String) {
        val err = if (v.isBlank()) null else validateNameLettersOnly(v)
        _register.update { s -> s.copy(apellido = v, apellidoError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterRutChange(v: String) {
        val rutErr = validateRut(v)
        val dvErr = validateDv(_register.value.dv, v)
        _register.update { s -> s.copy(rut = v, rutError = rutErr, dvError = dvErr).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
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
        val passErr = validateStrongPassword(v)
        val confErr = validateConfirm(v, _register.value.confirm)
        _register.update { s -> s.copy(pass = v, passError = passErr, confirmError = confErr).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }
    fun onRegisterConfirmChange(v: String) {
        val err = validateConfirm(_register.value.pass, v)
        _register.update { s -> s.copy(confirm = v, confirmError = err).apply { copy(canSubmit = checkRegisterCanSubmit(this)) } }
    }

    private fun checkRegisterCanSubmit(s: RegisterUiState): Boolean {
        val noErrors = s.nombreError == null && s.apellidoError == null &&
                s.rutError == null && s.dvError == null &&
                s.emailError == null && s.phoneError == null &&
                s.passError == null && s.confirmError == null
        val fieldsFilled = s.nombre.isNotBlank() && s.rut.isNotBlank() && s.dv.isNotBlank() &&
                s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        return noErrors && fieldsFilled
    }

    fun submitRegister() {
        if (!_register.value.canSubmit) return
        _register.update { it.copy(isSubmitting = true, errorMsg = null) }
        viewModelScope.launch {
            val req = RegisterRequest(_register.value.nombre, _register.value.apellido, _register.value.email, _register.value.pass, _register.value.confirm, _register.value.rut, _register.value.dv, _register.value.phone)
            val res = userRepository.register(req)
            _register.update { if (res.isSuccess) it.copy(success = true, isSubmitting = false) else it.copy(success = false, isSubmitting = false, errorMsg = res.exceptionOrNull()?.message) }
        }
    }

    // --- RESET PASSWORD ---
    fun onResetEmailChange(v: String) { _resetPassword.update { it.copy(email = v, emailError = validateEmail(v)) } }
    fun onResetNewPassChange(v: String) { _resetPassword.update { it.copy(newPass = v, passError = validateStrongPassword(v)) } }
    fun onResetConfirmChange(v: String) { _resetPassword.update { it.copy(confirm = v, confirmError = validateConfirm(it.newPass, v)) } }
    fun submitResetPassword() { _resetPassword.update { it.copy(isSubmitting = true) }; viewModelScope.launch { delay(1500); _resetPassword.update { it.copy(success = true, isSubmitting = false) } } }
    fun clearResetPasswordState() { _resetPassword.value = ResetPasswordUiState() }

    // --- PERFIL DE USUARIO ---

    fun loadUserProfile() {
        _profile.update { it.copy(isLoading = true, errorMsg = null) }
        viewModelScope.launch {
            val emailGuardado = userPreferences.getEmail()
            if (emailGuardado.isNullOrBlank()) {
                _profile.update { it.copy(isLoading = false) }
                return@launch
            }
            val result = userRepository.getProfile(emailGuardado)
            if (result.isSuccess) {
                val data = result.getOrNull()
                _profile.update {
                    it.copy(
                        isLoading = false,
                        nombre = data?.nombre ?: "",
                        apellido = data?.apellido ?: "",
                        email = data?.email ?: "",
                        telefono = data?.telefono ?: "",
                        direccion = data?.direccion ?: ""
                    )
                }
            } else {
                val errorReal = result.exceptionOrNull()?.message ?: "Error desconocido"
                _profile.update { it.copy(isLoading = false, errorMsg = errorReal) }
            }
        }
    }

    fun startEditing() { _profile.update { it.copy(isEditing = true) } }

    fun cancelEditing() {
        _profile.update { it.copy(isEditing = false) }
        loadUserProfile()
    }

    fun onProfileChange(nombre: String? = null, apellido: String? = null, telefono: String? = null, direccion: String? = null, pass: String? = null, confirm: String? = null) {
        _profile.update {
            it.copy(
                nombre = nombre ?: it.nombre,
                apellido = apellido ?: it.apellido,
                telefono = telefono ?: it.telefono,
                direccion = direccion ?: it.direccion,
                password = pass ?: it.password,
                confirmPassword = confirm ?: it.confirmPassword
            )
        }
    }

    fun saveProfile() {
        val p = _profile.value
        if (p.password.isNotEmpty() && p.password != p.confirmPassword) {
            _profile.update { it.copy(errorMsg = "Las contraseñas no coinciden") }
            return
        }

        _profile.update { it.copy(isLoading = true, errorMsg = null, updateSuccess = false) }
        viewModelScope.launch {
            val req = UpdateProfileRequest(
                nombre = p.nombre,
                apellido = p.apellido,
                telefono = p.telefono,
                direccion = p.direccion,
                password = if (p.password.isBlank()) null else p.password,
                confirmPassword = if (p.password.isBlank()) null else p.confirmPassword
            )
            val result = userRepository.updateProfile(req)
            _profile.update {
                it.copy(
                    isLoading = false,
                    updateSuccess = result.isSuccess,
                    isEditing = !result.isSuccess,
                    errorMsg = if(result.isSuccess) null else result.exceptionOrNull()?.message ?: "Error al actualizar"
                )
            }
            if (result.isSuccess) loadUserProfile()
        }
    }

    // --- CARRITO ---
    fun addProductToCart(p: Product) { viewModelScope.launch { cartRepository.addOrIncrement("product", p.id.toLong(), p.name, p.price, p.imageUrl) } }
    fun addCuadroToCart(c: Cuadro) { viewModelScope.launch { cartRepository.addOrIncrement("cuadro", c.id.toLong(), c.title, c.price, c.imageUrl) } }
    fun updateCartQuantity(i: CartItemEntity, q: Int) { viewModelScope.launch { cartRepository.updateQuantity(i, q) } }
    fun removeFromCart(i: CartItemEntity) { viewModelScope.launch { cartRepository.remove(i) } }
    fun clearCart() { viewModelScope.launch { cartRepository.clear() } }

    // --- PEDIDOS (CORREGIDO) ---
    fun recordOrder(items: List<CartItemEntity>, total: Int) {
        if (orderRepository == null) return
        viewModelScope.launch {
            // CORRECCIÓN 2: Usar email real para el pedido
            val email = userPreferences.getEmail()

            if (email.isNullOrBlank()) {
                // Si no hay email, no podemos crear el pedido.
                // Aquí podrías mostrar un error o redirigir al login.
                return@launch
            }

            val detalles = items.map { OrderDetail(it.refId, it.name, it.quantity, it.price.toDouble()) }
            val res = orderRepository.createOrder(email, OrderRequest(detalles))
            if (res.isSuccess) {
                clearCart()
                fetchOrders()
                showPurchaseNotification()
            }
        }
    }

    private fun showPurchaseNotification() {
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

    fun onAddProductCategoryChange(categoryId: Long) {
        _addProduct.update { it.copy(categoryId = categoryId) }
    }

    fun onImageSelected(uri: Uri?) { _addProduct.update { it.copy(imageUri = uri) } }

    fun saveProduct(ctx: Context) {
        val s = _addProduct.value
        _addProduct.update { it.copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val imageString = s.imageUri?.toString() ?: ""

            val req = CreateProductRequest(s.name, s.description, s.price.toDoubleOrNull()?:0.0, 10, imageString, CategoryIdRequest(s.categoryId))
//            val ok = productRepository.createProduct(req)
            val ok = false
            withContext(Dispatchers.Main) {
                _addProduct.update { it.copy(isSaving = false, saveSuccess = ok) }
                if (ok) fetchCatalog() else Toast.makeText(ctx, "Error al crear", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun clearAddProductState() { _addProduct.value = AddProductUiState(categoryId = ID_DEFECTO_MOLDURAS) }

    fun onAddCuadroChange(title: String?=null, description: String?=null, price: String?=null, size: String?=null, material: String?=null, category: String?=null, artist: String?=null) {
        _addCuadro.update { it.copy(title=title?:it.title, description=description?:it.description, price=price?:it.price, size=size?:it.size, material=material?:it.material, category=category?:it.category, artist=artist?:it.artist, canSubmit=true) }
    }
    fun onCuadroImageSelected(uri: Uri?) { _addCuadro.update { it.copy(imageUri = uri) } }

    fun saveCuadro(ctx: Context) {
        val s = _addCuadro.value
        _addCuadro.update { it.copy(isSaving = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val desc = "${s.description} | ${s.size} | ${s.material}"
            val imageString = s.imageUri?.toString() ?: ""

            val req = CreateProductRequest(s.title, desc, s.price.toDoubleOrNull()?:0.0, 5, imageString, CategoryIdRequest(ID_CATEGORIA_CUADROS))
//            val ok = productRepository.createProduct(req)
            val ok = false
            withContext(Dispatchers.Main) {
                _addCuadro.update { it.copy(isSaving = false, saveSuccess = ok) }
                if (ok) fetchCatalog() else Toast.makeText(ctx, "Error al crear", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun clearAddCuadroState() { _addCuadro.value = AddCuadroUiState() }

    // --- BORRAR ---
    fun deleteProduct(p: Product) {
        viewModelScope.launch(Dispatchers.IO) {
//            if (productRepository.deleteProduct(p.id.toString())) fetchCatalog()
        }
    }

    fun deleteCuadro(c: Cuadro) {
        viewModelScope.launch(Dispatchers.IO) {
//            if (productRepository.deleteProduct(c.id.toString())) fetchCatalog()
        }
    }

    // --- OTROS Y LOGOUT ---
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _isAdminSession.value = false
            // CORRECCIÓN 3: Limpiar datos al salir
            _orders.value = emptyList()
            _profile.value = ProfileUiState()
        }
    }

    fun clearLoginResult() { _login.value = LoginUiState() }
    fun clearRegisterResult() { _register.value = RegisterUiState() }

    fun createTempImageUri(): Uri {
        val context = getApplication<Application>().applicationContext
        val imageDir = File(context.cacheDir, "images").apply { mkdirs() }
        val tempFile = File.createTempFile("product_${System.currentTimeMillis()}", ".jpg", imageDir)
        return FileProvider.getUriForFile(context, "com.example.legacyframeapp.fileprovider", tempFile)
    }

    fun updateProductImage(ctx: Context, id: String, uri: Uri, onDone: (Boolean, String?) -> Unit) { onDone(true, null) }

    suspend fun setThemeMode(m: String) = userPreferences.setThemeMode(m)
    suspend fun setAccentColor(c: String) = userPreferences.setAccentColor(c)
    suspend fun setFontScale(s: Float) = userPreferences.setFontScale(s)

    suspend fun setNotifOffers(b: Boolean) = userPreferences.setNotifOffers(b)
    suspend fun setNotifTracking(b: Boolean) = userPreferences.setNotifTracking(b)
    suspend fun setNotifCart(b: Boolean) = userPreferences.setNotifCart(b)
}
