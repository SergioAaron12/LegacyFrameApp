package com.example.legacyframeapp.ui.screen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera // Icono para Cámara
import androidx.compose.material.icons.filled.PhotoLibrary // Icono para Galería
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
// Evitamos usar BuildConfig aquí; usaremos context.packageName para el FileProvider
import android.net.Uri
import java.io.File

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun AddProductScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit // Acción para volver atrás
) {
    val state by vm.addProduct.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Photo Picker (galería)
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            vm.onAddProductChange(imageUri = uri.toString())
        }
    }

    // Cámara: tomar foto y guardarla en un archivo temporal vía FileProvider
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val current = cameraUri
            if (current != null) {
                vm.onAddProductChange(imageUri = current.toString())
            }
        }
    }

    fun launchCamera() {
        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
        val photoFile = File(imagesDir, "camera_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        cameraUri = uri
        takePictureLauncher.launch(uri)
    }

    // Cuando 'saveSuccess' se vuelve true, resetea el estado y vuelve atrás
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            vm.clearAddProductState() // Limpia el formulario en el VM
            onNavigateBack() // Navega a la pantalla anterior
        }
    }

    AddProductScreen(
        name = state.name,
        description = state.description,
        price = state.price,
        imageUri = state.imageUri, // (Aún no lo usamos en la UI)
        nameError = state.nameError,
        priceError = state.priceError,
        imageError = state.imageError,
        isSaving = state.isSaving,
        canSubmit = state.canSubmit,
        errorMsg = state.errorMsg,
        onNameChange = { vm.onAddProductChange(name = it) },
        onDescriptionChange = { vm.onAddProductChange(description = it) },
        onPriceChange = { vm.onAddProductChange(price = it) },
        onSelectImageGallery = {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCapturePhoto = { launchCamera() },
        onSubmit = { vm.saveProduct(context) },
        onBack = onNavigateBack // Pasa la acción de "volver"
    )
}

// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    name: String, description: String, price: String, imageUri: String,
    nameError: String?, priceError: String?, imageError: String?,
    isSaving: Boolean, canSubmit: Boolean, errorMsg: String?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onSelectImageGallery: () -> Unit,
    onCapturePhoto: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Moldura") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Para que el formulario no se corte
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Campo Nombre ---
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre del producto") },
                isError = nameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError != null) {
                Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            // --- Campo Descripción ---
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp), // Más alto
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // --- Campo Precio ---
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Precio (Ej: 25000)") },
                isError = priceError != null,
                singleLine = true,
                // Solo números
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                prefix = { Text("$ ") }, // Prefijo de peso
                modifier = Modifier.fillMaxWidth()
            )
            if (priceError != null) {
                Text(priceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // --- Botones Imagen: Galería y Cámara ---
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onSelectImageGallery,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Galería")
                }
                OutlinedButton(
                    onClick = onCapturePhoto,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Cámara")
                }
            }
            if (imageError != null) {
                Text(imageError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            // (Aquí mostraremos la imagen previsualizada 'imageUri' más adelante)

            Spacer(Modifier.height(16.dp))

            // --- Botón Guardar ---
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Producto")
                }
            }

            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
