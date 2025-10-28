package com.example.legacyframeapp.ui.viewmodel

import android.app.Application
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.data.local.user.UserEntity
import com.example.legacyframeapp.data.repository.CartRepository
import com.example.legacyframeapp.data.repository.CuadroRepository
import com.example.legacyframeapp.data.repository.ProductRepository
import com.example.legacyframeapp.data.repository.UserRepository
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.domain.validation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import com.example.legacyframeapp.data.local.storage.UserPreferences
import com.example.legacyframeapp.data.local.order.OrderEntity
import com.example.legacyframeapp.data.repository.OrderRepository
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
    val currentUser: UserEntity? = null,
    val isAdmin: Boolean = false
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
    private val orderRepository: OrderRepository? = null
) : AndroidViewModel(application) {

    // --- (Login, Register, Session) ---
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register.asStateFlow()

    private val _session = MutableStateFlow(SessionUiState())
    val session: StateFlow<SessionUiState> = _session.asStateFlow()

    // --- (Productos - Molduras) ---
    val products: StateFlow<List<ProductEntity>> =
        productRepository.getAllProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _productCategories = MutableStateFlow<List<String>>(emptyList())
    val productCategories: StateFlow<List<String>> = _productCategories.asStateFlow()

    private val _productFilter = MutableStateFlow("all")
    val productFilter: StateFlow<String> = _productFilter.asStateFlow()

    // Flujo filtrado de Productos
    val filteredProducts: StateFlow<List<ProductEntity>> =
        combine(products, productFilter) { productsList, filter ->
            if (filter == "all") productsList else productsList.filter { it.category.equals(filter, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())


    // --- (Cuadros) ---
    val cuadros: StateFlow<List<CuadroEntity>> =
        cuadroRepository.getAllCuadros()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _cuadroCategories = MutableStateFlow<List<String>>(emptyList())
    val cuadroCategories: StateFlow<List<String>> = _cuadroCategories.asStateFlow()

    private val _cuadroFilter = MutableStateFlow("all")
    val cuadroFilter: StateFlow<String> = _cuadroFilter.asStateFlow()

    // --- DEFINICIÓN AÑADIDA ---
    val filteredCuadros: StateFlow<List<CuadroEntity>> =
        combine(cuadros, cuadroFilter) { cuadrosList, filter -> // Combina cuadros y filtro
            if (filter == "all") {
                cuadrosList // Si el filtro es "all", devuelve todos
            } else {
                cuadrosList.filter { it.category.equals(filter, ignoreCase = true) } // Filtra por categoría
            }
        }.stateIn( // Convierte el resultado en un StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList() // Empieza vacío
        )
    // -------------------------

    // --- (Carrito) ---
    val cartItems: StateFlow<List<CartItemEntity>> = cartRepository.items()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    val cartTotal: StateFlow<Int> = cartRepository.total()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    val cartItemCount: StateFlow<Int> = cartRepository.count()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    // --- (Preferencias) ---
    val darkMode: StateFlow<Boolean> =
        userPreferences.isDarkMode
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val avatarType: StateFlow<String> =
        userPreferences.avatarType
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), "male")

    // --- (Historial de compras) ---
    val orders: StateFlow<List<OrderEntity>> =
        (orderRepository?.getAll() ?: flowOf(emptyList())) // Usa flowOf si es nulo
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    // --- (Add Product) ---
    private val _addProduct = MutableStateFlow(AddProductUiState())
    val addProduct: StateFlow<AddProductUiState> = _addProduct.asStateFlow()

    // --- (Add Cuadro) ---
    private val _addCuadro = MutableStateFlow(AddCuadroUiState())
    val addCuadro: StateFlow<AddCuadroUiState> = _addCuadro.asStateFlow()


    // --- BLOQUE init ---
    init {
        viewModelScope.launch {
            // Cargar categorías
            _productCategories.value = productRepository.getAllCategories()
            _cuadroCategories.value = cuadroRepository.getAllCategories()
            userRepository.ensureAdminUserExists()
        }
        // Restaurar estado de sesión basado en preferencias
        viewModelScope.launch {
            userPreferences.isLoggedIn.collect { loggedIn ->
                if (!loggedIn && session.value.isLoggedIn) {
                    // Si DataStore dice no logueado pero la sesión sí, desloguear
                    logout()
                }
                // (La lógica de login actualiza DataStore y la sesión, así que no necesitamos más aquí)
            }
        }
    }

    // --- Funciones de Prefetch ---
    fun prefetchProductImages(appContext: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val current = productRepository.getAllProducts().first()
                current.forEach { p ->
                    if (p.imagePath.startsWith("http")) {
                        val safeName = sanitizeFileName(p.name.ifBlank { p.imagePath.hashCode().toString() }) + ".jpg"
                        val outFile = File(appContext.filesDir, safeName)
                        val ok = downloadToFile(p.imagePath, outFile)
                        if (ok) {
                            productRepository.update(p.copy(imagePath = outFile.name)) // Guarda solo el nombre
                        }
                    }
                }
            } catch (_: Exception) { }
        }
    }
    private suspend fun downloadToFile(url: String, dest: File): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            URL(url).openStream().use { input ->
                FileOutputStream(dest).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
    private fun sanitizeFileName(name: String): String =
        name.lowercase()
            .replace(" ", "_")
            .replace(Regex("[^a-z0-9._-]"), "")


    // --- Funciones de Login/Registro ---
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
        val apellidoError = validateApellido(apellido)
        _register.update { s ->
            s.copy(
                apellido = apellido,
                apellidoError = apellidoError,
                canSubmit = checkRegisterCanSubmit(s.copy(apellido = apellido, apellidoError = apellidoError))
            )
        }
    }
    fun onRegisterRutChange(rut: String) {
        val rutError = validateRut(rut)
        val dvError = validateDv(_register.value.dv, rut)
        _register.update { s ->
            s.copy(
                rut = rut,
                rutError = rutError,
                dvError = dvError,
                canSubmit = checkRegisterCanSubmit(s.copy(rut = rut, rutError = rutError, dvError = dvError))
            )
        }
    }
    fun onRegisterDvChange(dv: String) {
        val dvError = validateDv(dv, _register.value.rut)
        _register.update { s ->
            s.copy(
                dv = dv.uppercase(),
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
        val phoneError = validatePhoneDigitsOnly(phone)
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
        val confirmError = validateConfirm(pass, _register.value.confirm)
        _register.update { s ->
            s.copy(
                pass = pass,
                passError = passError,
                confirmError = confirmError,
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
    private fun checkRegisterCanSubmit(s: RegisterUiState): Boolean {
        return s.nombreError == null && s.apellidoError == null &&
                s.rutError == null && s.dvError == null &&
                s.emailError == null && s.phoneError == null &&
                s.passError == null && s.confirmError == null &&
                s.nombre.isNotBlank() && s.rut.isNotBlank() && s.dv.isNotBlank() &&
                s.email.isNotBlank() && s.phone.isNotBlank() &&
                s.pass.isNotBlank() && s.confirm.isNotBlank()
    }

    fun submitLogin() {
        val s = _login.value
        if (s.emailError != null || s.passError != null || s.email.isBlank() || s.pass.isBlank()) {
            _login.update { it.copy(isSubmitting = false) }
            return
        }
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            val result = userRepository.login(s.email, s.pass)
            _login.update { loginState ->
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    val isAdmin = (user?.rolId == 1) ||
                            (s.email.equals("admin@legacyframes.cl", ignoreCase = true) && s.pass == "Admin123!")
                    _session.update { sessionState ->
                        sessionState.copy(isLoggedIn = true, currentUser = user, isAdmin = isAdmin)
                    }
                    viewModelScope.launch { userPreferences.setLoggedIn(true) }
                    loginState.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    loginState.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Credenciales inválidas")
                }
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            _session.update { SessionUiState() }
            _login.update { LoginUiState() }
            userPreferences.setLoggedIn(false)
        }
    }
    fun submitRegister() {
        val s = _register.value
        val canSubmitFinal = checkRegisterCanSubmit(s)
        if (!canSubmitFinal) {
            _register.update { it.copy(
                nombreError = it.nombreError ?: if(it.nombre.isBlank()) "El nombre es obligatorio" else null,
                apellidoError = it.apellidoError,
                rutError = it.rutError ?: if(it.rut.isBlank()) "El RUT es obligatorio" else null,
                dvError = it.dvError ?: if(it.dv.isBlank()) "El DV es obligatorio" else validateDv(it.dv, it.rut),
                emailError = it.emailError ?: if(it.email.isBlank()) "El email es obligatorio" else null,
                phoneError = it.phoneError ?: if(it.phone.isBlank()) "El teléfono es obligatorio" else null,
                passError = it.passError ?: if(it.pass.isBlank()) "La contraseña es obligatoria" else null,
                confirmError = it.confirmError ?: if(it.confirm.isBlank()) "Confirma la contraseña" else if (it.pass != it.confirm) "Las contraseñas no coinciden" else null
            )}
            return
        }
        if (s.isSubmitting) return
        val phoneInt: Int
        try { phoneInt = s.phone.toInt() } catch (e: NumberFormatException) {
            _register.update { it.copy(isSubmitting = false, errorMsg = "Error interno: El teléfono no es un número válido.") }
            return
        }
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            val result = userRepository.register(
                nombre = s.nombre, apellido = s.apellido.ifBlank { null }, rut = s.rut, dv = s.dv,
                email = s.email, password = s.pass, phone = phoneInt
            )
            _register.update {
                if (result.isSuccess) { it.copy(isSubmitting = false, success = true, errorMsg = null) }
                else { it.copy(isSubmitting = false, success = false, errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar") }
            }
        }
    }
    fun clearLoginResult() { _login.update { it.copy(success = false, errorMsg = null) } }
    fun clearRegisterResult() { _register.update { it.copy(success = false, errorMsg = null) } }


    // --- LÓGICA DE AddProduct ---
    fun onAddProductChange(
        name: String = _addProduct.value.name,
        description: String = _addProduct.value.description,
        price: String = _addProduct.value.price
    ) {
        val nameError = if(name.isBlank()) "El nombre es obligatorio" else null
        val priceError = validatePrice(price) // Usa el validador general
        val currentImageUri = _addProduct.value.imageUri
        _addProduct.update {
            it.copy(
                name = name, description = description, price = price,
                nameError = nameError, priceError = priceError,
                canSubmit = nameError == null && priceError == null && currentImageUri != null && !it.isSaving
            )
        }
    }
    fun onImageSelected(uri: Uri?) { // Para AddProduct
        val nameError = _addProduct.value.nameError
        val priceError = _addProduct.value.priceError
        _addProduct.update {
            it.copy(
                imageUri = uri,
                imageError = if (uri == null) "Debe seleccionar una imagen" else null,
                canSubmit = nameError == null && priceError == null && uri != null && !it.isSaving
            )
        }
    }
    fun saveProduct(appContext: Context) {
        val s = _addProduct.value
        if (!s.canSubmit || s.isSaving) return
        if (s.imageUri == null) {
            _addProduct.update { it.copy(imageError = "Debe seleccionar una imagen", isSaving = false) }
            return
        }
        _addProduct.update { it.copy(isSaving = true, errorMsg = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val priceInt = s.price.toInt()
                val finalImageName = ImageStorageHelper.saveImageToInternalStorage(appContext, s.imageUri)
                val newProduct = ProductEntity(
                    name = s.name.trim(), description = s.description.trim(), price = priceInt,
                    imagePath = finalImageName, category = "otros"
                )
                productRepository.insert(newProduct)
                withContext(Dispatchers.Main) {
                    _addProduct.update { it.copy(isSaving = false, saveSuccess = true) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _addProduct.update { it.copy(isSaving = false, errorMsg = e.message ?: "Error al guardar") }
                }
            }
        }
    }
    fun clearAddProductState() { _addProduct.update { AddProductUiState() } }
    fun createTempImageUri(): Uri {
        val context = getApplication<Application>().applicationContext
        val imageDir = File(context.cacheDir, "images").apply { mkdirs() }
        val tempFile = File.createTempFile("product_${System.currentTimeMillis()}", ".jpg", imageDir)
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, tempFile)
    }


    // --- LÓGICA DE AddCuadro ---
    fun onAddCuadroChange(
        title: String = _addCuadro.value.title,
        description: String = _addCuadro.value.description,
        price: String = _addCuadro.value.price,
        size: String = _addCuadro.value.size,
        material: String = _addCuadro.value.material,
        category: String = _addCuadro.value.category,
        artist: String? = _addCuadro.value.artist
    ) {
        val titleError = if (title.isBlank()) "El título es obligatorio" else null
        val priceError = when {
            price.isBlank() -> "El precio es obligatorio"
            !price.all { it.isDigit() } -> "Solo números"
            price.length > 6 -> "Máximo 6 dígitos"
            price.toIntOrNull() == null -> "Precio inválido"
            else -> null // Sin error
        }
        val currentImageUri = _addCuadro.value.imageUri
        _addCuadro.update {
            it.copy(
                title = title, description = description, price = price,
                size = size, material = material, category = category, artist = artist,
                titleError = titleError, priceError = priceError,
                canSubmit = titleError == null && priceError == null && currentImageUri != null && !it.isSaving
            )
        }
    }
    fun onCuadroImageSelected(uri: Uri?) { // Para AddCuadro
        val titleError = _addCuadro.value.titleError
        val priceError = _addCuadro.value.priceError
        _addCuadro.update {
            it.copy(
                imageUri = uri,
                imageError = if (uri == null) "Debe seleccionar una imagen" else null,
                canSubmit = titleError == null && priceError == null && uri != null && !it.isSaving
            )
        }
    }
    fun saveCuadro(appContext: Context) {
        val s = _addCuadro.value
        if (!s.canSubmit || s.isSaving) return
        if (s.imageUri == null) {
            _addCuadro.update { it.copy(imageError = "Debe seleccionar una imagen", isSaving = false) }
            return
        }
        _addCuadro.update { it.copy(isSaving = true, errorMsg = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val priceInt = s.price.toInt()
                val savedName = ImageStorageHelper.saveImageToInternalStorage(appContext, s.imageUri)
                val entity = CuadroEntity(
                    title = s.title.trim(), description = s.description.trim(), price = priceInt,
                    size = s.size.trim().ifBlank { "" }, material = s.material.trim().ifBlank { "" },
                    category = s.category.trim().ifBlank { "otros" }, imagePath = savedName,
                    isCustom = false, artist = s.artist?.trim()?.ifBlank { null }
                )
                cuadroRepository.insert(entity)
                withContext(Dispatchers.Main) {
                    _addCuadro.update { it.copy(isSaving = false, saveSuccess = true) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _addCuadro.update { it.copy(isSaving = false, errorMsg = e.message ?: "Error al guardar") }
                }
            }
        }
    }
    fun clearAddCuadroState() { _addCuadro.update { AddCuadroUiState() } }


    // --- Lógica Admin (Eliminar, Cambiar Imagen) ---
    fun deleteProduct(product: ProductEntity) { viewModelScope.launch { productRepository.delete(product) } }
    fun deleteCuadro(cuadro: CuadroEntity) { viewModelScope.launch { cuadroRepository.delete(cuadro) } }
    fun updateProductImage(appContext: Context, productId: Long, newImageUri: Uri, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val product = productRepository.getProductById(productId)
                    ?: throw NoSuchElementException("Producto no encontrado")
                val savedPath = ImageStorageHelper.saveImageToInternalStorage(appContext, newImageUri)
                productRepository.update(product.copy(imagePath = savedPath))
                withContext(Dispatchers.Main) { onDone(true, null) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onDone(false, e.message ?: "Error al actualizar imagen") }
            }
        }
    }


    // --- Filtros ---
    fun setProductFilter(category: String) { _productFilter.value = category }
    fun setCuadroFilter(category: String) { _cuadroFilter.value = category }

    // --- Carrito ---
    fun addProductToCart(p: ProductEntity) {
        viewModelScope.launch {
            cartRepository.addOrIncrement("product", p.id, p.name, p.price, p.imagePath)
        }
    }
    fun addCuadroToCart(c: CuadroEntity) {
        viewModelScope.launch {
            cartRepository.addOrIncrement("cuadro", c.id, c.title, c.price, c.imagePath)
        }
    }
    fun updateCartQuantity(item: CartItemEntity, newQty: Int) {
        viewModelScope.launch { cartRepository.updateQuantity(item, newQty) }
    }
    fun removeFromCart(item: CartItemEntity) {
        viewModelScope.launch { cartRepository.remove(item) }
    }
    fun clearCart() { viewModelScope.launch { cartRepository.clear() } }

    // --- Notificación de Compra ---
    fun showPurchaseNotification() {
        val context = getApplication<Application>().applicationContext
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val builder = NotificationCompat.Builder(context, "purchase_notifications") // Usa el ID del canal
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Compra Exitosa!")
            .setContentText("Tu pedido ha sido registrado.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build()) // Usa un ID único (ej: timestamp)
        }
    }

    // --- Preferencias ---
    suspend fun setDarkMode(enabled: Boolean) { userPreferences.setDarkMode(enabled) }
    suspend fun setAvatarType(type: String) { userPreferences.setAvatarType(type) }

    // --- Historial de Compras ---
    fun recordOrder(items: List<CartItemEntity>, total: Int) {
        val repo = orderRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val summary = items.joinToString("\n") { "- ${it.name} x${it.quantity} = ${formatPriceInternal(it.price * it.quantity)}" }
            repo.insert(OrderEntity(dateMillis = System.currentTimeMillis(), itemsText = summary, total = total))
        }
    }
    // Función interna para formatear precio
    private fun formatPriceInternal(value: Int): String {
        return try {
            // Intenta usar NumberFormat para formato localizado con puntos
            val format = NumberFormat.getNumberInstance(Locale("es", "CL"))
            "$ ${format.format(value)}"
        } catch (e: Exception) {
            "$ $value" // Fallback simple
        }
    }

    // --- Funciones de validación ---
    private fun validatePrice(price: String): String? {
        if (price.isBlank()) return "El precio es obligatorio"
        if (!price.all { it.isDigit() }) return "Solo números"
        if (price.length > 9) return "Máximo 9 dígitos"
        try {
            if (price.length == 9 && price > Int.MAX_VALUE.toString().take(9)) {
                return "Precio demasiado grande"
            }
            price.toInt()
        } catch (e: NumberFormatException) {
            return "Precio inválido o demasiado grande"
        }
        return null
    }

}