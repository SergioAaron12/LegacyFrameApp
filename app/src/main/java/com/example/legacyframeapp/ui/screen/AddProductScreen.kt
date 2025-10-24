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

    // Cuando 'saveSuccess' se vuelve true, resetea el estado y vuelve atrás
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            vm.clearAddProductState() // Limpia el formulario en el VM
            onNavigateBack() // Navega a la pantalla anterior
        }
    }
    // --- MANEJO DEL DIÁLOGO DE SELECCIÓN ---
    // 'show' controla si el diálogo está visible
    var showDialog by remember { mutableStateOf(false) }
    // 'tempUri' guardará el Uri de la cámara mientras se toma la foto
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    // -------------------------------------

    // Si el diálogo debe mostrarse, lo componemos
    if (showDialog) {
        ImagePickerSelector(
            onDismiss = { showDialog = false }, // Cierra el diálogo
            onGallery = {
                showDialog = false
                // (La lógica de galería se moverá al 'AddProductScreen' de abajo)
            },
            onCamera = {
                showDialog = false
                tempUri = vm.createTempImageUri() // 1. Creamos el Uri
                // (La lógica de cámara se moverá al 'AddProductScreen' de abajo)
            }
        )
    }

    AddProductScreen(
        name = state.name,
        description = state.description,
        price = state.price,
        imageUri = state.imageUri, // <--- PASAR EL URI
        nameError = state.nameError,
        priceError = state.priceError,
        imageError = state.imageError, // <--- PASAR EL ERROR DE IMAGEN
        isSaving = state.isSaving,
        canSubmit = state.canSubmit,
        errorMsg = state.errorMsg,
        onNameChange = { vm.onAddProductChange(name = it) },
        onDescriptionChange = { vm.onAddProductChange(description = it) },
        onPriceChange = { vm.onAddProductChange(price = it) },
        onImageSelected = { uri -> vm.onImageSelected(uri) },
        onSubmit = { vm.saveProduct() },
        onBack = onNavigateBack,
        onShowImagePicker = { showDialog = true }, // <-- Acción para MOSTRAR el diálogo
        tempCameraUri = tempUri // <-- Pasamos el Uri temporal
    )
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

    // --- NUEVOS PARÁMETROS ---
    onShowImagePicker: () -> Unit,
    tempCameraUri: Uri? // El Uri temporal creado por el VM
) {
    // --- 1. LANZADOR DE GALERÍA (Photo Picker) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onImageSelected(uri) // Llama al ViewModel con el Uri seleccionado
        }
    )

    // --- 2. LANZADOR DE CÁMARA ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Si la foto se tomó (success=true),
                // el 'tempCameraUri' ya contiene la foto.
                onImageSelected(tempCameraUri)
            }
            // Si el usuario canceló (success=false), no hacemos nada
        }
    )

    // --- 3. LANZADOR DE PERMISO DE GALERÍA ---
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permiso concedido, lanzar la galería
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
                // Permiso concedido, lanzar la cámara
                // El 'tempCameraUri' NO debe ser nulo aquí
                tempCameraUri?.let { uri ->
                    cameraLauncher.launch(uri)
                }
            } else {
                println("Permiso de cámara denegado")
            }
        }
    )

    // --- Lógica del Diálogo (movida desde el Vm) ---
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ImagePickerSelector(
            onDismiss = { showDialog = false },
            onGallery = {
                // --- Lógica de Galería ---
                showDialog = false
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            },
            onCamera = {
                // --- Lógica de Cámara ---
                showDialog = false
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }
    // --------------------------------------

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // ... (Tu TopBar sigue igual) ...
                title = { Text("Añadir Moldura") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ... (Campo Nombre, Campo Descripción, Campo Precio siguen igual) ...

            // --- VISTA PREVIA DE LA IMAGEN ---
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri, // Carga la imagen desde el Uri
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f) // Proporción 16:9
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // --- Botón Seleccionar Imagen ---
            OutlinedButton(
                onClick = {
                    // --- ¡Ahora solo muestra el diálogo! ---
                    onShowImagePicker()
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

            // --- Botón Guardar ---
            Button(
                // ... (Tu botón de guardar sigue igual) ...
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
