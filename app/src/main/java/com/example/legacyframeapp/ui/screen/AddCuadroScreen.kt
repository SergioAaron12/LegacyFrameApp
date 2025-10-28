package com.example.legacyframeapp.ui.screen

import android.Manifest // Necesario para permisos
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Icono de flecha actualizado
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment // Necesario si usas Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.ui.components.AppButton // Asume que tienes este componente
import com.example.legacyframeapp.ui.components.ThousandSeparatorTransformation // Asume que tienes esto
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// --- Stateful Composable (Conecta con ViewModel) ---
@Composable
fun AddCuadroScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val state by vm.addCuadro.collectAsStateWithLifecycle()
    val context = LocalContext.current // Necesitamos el contexto para saveCuadro

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            vm.clearAddCuadroState()
            onNavigateBack()
        }
    }

    AddCuadroScreen(
        // Estado
        title = state.title,
        description = state.description,
        price = state.price,
        size = state.size,
        material = state.material,
        category = state.category,
        imageUri = state.imageUri,
        titleError = state.titleError,
        priceError = state.priceError,
        imageError = state.imageError,
        isSaving = state.isSaving,
        canSubmit = state.canSubmit,
        errorMsg = state.errorMsg,

        // Acciones
        onTitleChange = { vm.onAddCuadroChange(title = it) },
        onDescriptionChange = { vm.onAddCuadroChange(description = it) },
        onPriceChange = { vm.onAddCuadroChange(price = it) },
        onSizeChange = { vm.onAddCuadroChange(size = it) },
        onMaterialChange = { vm.onAddCuadroChange(material = it) },
        onCategoryChange = { vm.onAddCuadroChange(category = it) },
        onImageSelected = { uri -> vm.onCuadroImageSelected(uri) },
        onSubmit = { vm.saveCuadro(context) }, // Pasa el contexto
        onBack = onNavigateBack,
        createTempImageUri = vm::createTempImageUri
    )
}

// --- Stateless Composable (Solo UI) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCuadroScreen(
    // Estado
    title: String, description: String, price: String, size: String,
    material: String, category: String, imageUri: Uri?,
    titleError: String?, priceError: String?, imageError: String?,
    isSaving: Boolean, canSubmit: Boolean, errorMsg: String?,
    // Acciones
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onMaterialChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    createTempImageUri: () -> Uri
) {
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // --- LANZADORES (Sin cambios recientes) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelected(uri) }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) onImageSelected(tempUri) }
    )
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else { println("Permiso de galería denegado") }
        }
    )
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                tempUri?.let { uri -> cameraLauncher.launch(uri) }
                    ?: println("Error: tempUri era nulo al lanzar la cámara")
            } else { println("Permiso de cámara denegado") }
        }
    )
    // ------------------------------------

    // Acciones para los botones de imagen
    val onSelectImageGallery = { galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES) }
    val onCapturePhoto = {
        tempUri = createTempImageUri()
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Nuevo Cuadro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Campos del formulario ---
            OutlinedTextField(
                value = title, onValueChange = onTitleChange, label = { Text("Título*") },
                isError = titleError != null, singleLine = true, // Añadido singleLine
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), // Añadido imeAction
                modifier = Modifier.fillMaxWidth()
            )
            if (titleError != null) { Text(titleError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            OutlinedTextField(
                value = description, onValueChange = onDescriptionChange, label = { Text("Descripción") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), // Añadido imeAction
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            // --- PRECIO CON LÍMITE DE 6 DÍGITOS ---
            OutlinedTextField(
                value = price,
                // OnValueChange modificado para limitar a 6 dígitos
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                        onPriceChange(newValue)
                    }
                },
                label = { Text("Precio (CLP)*") }, isError = priceError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                visualTransformation = ThousandSeparatorTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            // --------------------------------------
            if (priceError != null) { Text(priceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            OutlinedTextField(
                value = size, onValueChange = onSizeChange, label = { Text("Tamaño (ej: 50x70 cm)") },
                singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = material, onValueChange = onMaterialChange, label = { Text("Material (ej: Canvas)") },
                singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category, onValueChange = onCategoryChange, label = { Text("Categoría (ej: Paisajes)") },
                singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // Último campo
                modifier = Modifier.fillMaxWidth()
            )

            // Vista previa y botones de imagen
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri, contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }

            // Botones Galería / Cámara
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onSelectImageGallery, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Galería")
                }
                OutlinedButton(onClick = onCapturePhoto, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Cámara")
                }
            }
            if (imageError != null) {
                Text(imageError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            // Botón Guardar
            AppButton(
                onClick = onSubmit,
                enabled = canSubmit && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Guardar Cuadro")
                }
            }

            // Mensaje de error global
            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}