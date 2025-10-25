package com.example.legacyframeapp.ui.screen
// --- IMPORTS (SIN DUPLICADOS) ---
import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult // <-- SOLO UNA VEZ
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.platform.LocalContext // No se usa directamente aquí
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun AddProductScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val state by vm.addProduct.collectAsStateWithLifecycle()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            vm.clearAddProductState()
            onNavigateBack()
        }
    }

    AddProductScreen(
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
        onNameChange = { vm.onAddProductChange(name = it) },
        onDescriptionChange = { vm.onAddProductChange(description = it) },
        onPriceChange = { vm.onAddProductChange(price = it) },
        onImageSelected = { uri -> vm.onImageSelected(uri) },
        onSubmit = { vm.saveProduct() },
        onBack = onNavigateBack,
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
    createTempImageUri: () -> Uri
) {

    var showDialog by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // --- LANZADORES (CORREGIDOS Y COMPLETOS) ---
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
            } else {
                println("Permiso de galería denegado")
                // TODO: Mostrar mensaje al usuario
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                tempUri?.let { uri -> cameraLauncher.launch(uri) }
                    ?: println("Error: tempUri era nulo al lanzar la cámara") // Manejo de error
            } else {
                println("Permiso de cámara denegado")
                // TODO: Mostrar mensaje al usuario
            }
        }
    )
    // ----------------------------------------------

    if (showDialog) {
        ImagePickerSelector(
            onDismiss = { showDialog = false },
            onGallery = {
                showDialog = false
                // Siempre pide permiso antes de lanzar
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            },
            onCamera = {
                showDialog = false
                tempUri = createTempImageUri() // Crea ANTES de pedir permiso
                // Siempre pide permiso antes de lanzar
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Moldura") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors( // Asume que tienes colores definidos
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

            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre Moldura") },
                isError = nameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError != null) { Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción (Opcional)") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().height(100.dp) // Más alto
            )

            // Precio
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Precio (CLP)") },
                isError = priceError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
            if (priceError != null) { Text(priceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }


            // Vista previa de la imagen
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp)) // Espacio si hay imagen
            }

            // Botón Seleccionar Imagen
            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(if (imageUri == null) "Seleccionar Imagen" else "Cambiar Imagen")
            }
            if (imageError != null) {
                Text(imageError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            // Botón Guardar
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Guardar Producto")
                }
            }

            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

// --- ImagePickerSelector (Diálogo) ---
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
        confirmButton = { TextButton(onClick = onCamera) { Text("Cámara") } },
        dismissButton = { TextButton(onClick = onGallery) { Text("Galería") } }
    )
}