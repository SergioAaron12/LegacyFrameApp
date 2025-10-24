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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera // Icono para el botón de imagen
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import android.Manifest // Para el permiso
import android.net.Uri // Para el Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage // <-- La librería que importamos
import androidx.compose.material3.AlertDialog // Para el diálogo
import androidx.compose.material3.TextButton // Para los botones del diálogo
import androidx.compose.runtime.mutableStateOf // Para el estado del diálogo
import androidx.compose.runtime.remember // Para recordar el estado
import androidx.compose.runtime.setValue // Para 'by'


// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun AddProductScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val state by vm.addProduct.collectAsStateWithLifecycle()

    // El LaunchedEffect para navegar atrás cuando state.saveSuccess es true
    // sigue igual y está perfecto.
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            vm.clearAddProductState()
            onNavigateBack()
        }
    }

    // --- VERSIÓN SIMPLIFICADA ---
    // Ya no manejamos 'showDialog' ni 'tempUri' aquí.

    AddProductScreen(
        // Pasamos el estado (sin cambios)
        name = state.name,
        description = state.description,
        price = state.price,
        imageUri = state.imageUri,
        nameError = state.nameError,
        priceError = state.priceError,
        imageError = state.imageError,
        isSaving = state.isSaving,
        canSubmit = state.canSubmit,
        errorMsg = state.errorMsg,

        // Pasamos las acciones (sin cambios)
        onNameChange = { vm.onAddProductChange(name = it) },
        onDescriptionChange = { vm.onAddProductChange(description = it) },
        onPriceChange = { vm.onAddProductChange(price = it) },
        onImageSelected = { uri -> vm.onImageSelected(uri) },
        onSubmit = { vm.saveProduct() },
        onBack = onNavigateBack,

        // --- AÑADIMOS ESTO ---
        // Le pasamos la *función* para crear el Uri de la cámara
        createTempImageUri = vm::createTempImageUri
    )
}
// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    name: String, description: String, price: String, imageUri: Uri?,
    nameError: String?, priceError: String?, imageError: String?,
    isSaving: Boolean, canSubmit: Boolean, errorMsg: String?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,

    // --- PARÁMETRO NUEVO ---
    createTempImageUri: () -> Uri // Función que viene del VM
) {

    // --- ESTADO LOCAL PARA EL DIÁLOGO Y EL URI TEMPORAL ---
    var showDialog by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // --- 1. LANZADOR DE GALERÍA (Photo Picker) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onImageSelected(uri)
        }
    )

    // --- 2. LANZADOR DE CÁMARA ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Si la foto se tomó, usamos el tempUri que creamos
                onImageSelected(tempUri)
            }
        }
    )

    // --- 3. LANZADOR DE PERMISO DE GALERÍA ---
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                println("Permiso de galería denegado")
            }
        }
    )

    // --- 4. LANZADOR DE PERMISO DE CÁMARA ---
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // El 'tempUri' ya fue creado por el diálogo
                tempUri?.let { uri ->
                    cameraLauncher.launch(uri)
                }
            } else {
                println("Permiso de cámara denegado")
            }
        }
    )

    // --- Lógica del Diálogo (AHORA SÍ FUNCIONARÁ) ---
    if (showDialog) {
        ImagePickerSelector(
            onDismiss = { showDialog = false },
            onGallery = {
                showDialog = false
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            },
            onCamera = {
                showDialog = false
                // --- ARREGLO CLAVE ---
                // 1. Creamos el Uri temporal *antes* de pedir permiso
                tempUri = createTempImageUri()
                // 2. Pedimos el permiso
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }

    Scaffold(
        topBar = {
            // ... (Tu TopBar sigue igual) ...
            CenterAlignedTopAppBar(
                title = { Text("Añadir Moldura") },
                // ... (colors, navigationIcon)
            )
        }
    ) { innerPadding ->
        Column(
            // ... (Tu Column y campos de Texto siguen igual) ...
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {

            // ... (Campo Nombre, Desc, Precio) ...

            // --- VISTA PREVIA DE LA IMAGEN (sin cambios) ---
            if (imageUri != null) {
                // ... (Tu AsyncImage)
            }

            // --- Botón Seleccionar Imagen (MODIFICADO) ---
            OutlinedButton(
                onClick = {
                    // --- ARREGLO CLAVE ---
                    // Ahora cambia la variable 'showDialog' local
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(if (imageUri == null) "Seleccionar Imagen" else "Cambiar Imagen")
            }
            if (imageError != null) {
                Text(imageError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            // --- Botón Guardar (sin cambios) ---
            Button(
                onClick = onSubmit,
                // ... (el resto del botón sigue igual) ...
            ) {
                // ...
            }
        }
    }
}

@Composable
fun ImagePickerSelector(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Imagen") },
        text = { Text("¿Cómo deseas obtener la imagen?") },
        confirmButton = {
            TextButton(
                onClick = onCamera // Llama a la acción de la cámara
            ) {
                Text("Cámara")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onGallery // Llama a la acción de la galería
            ) {
                Text("Galería")
            }
        }
    )
}
