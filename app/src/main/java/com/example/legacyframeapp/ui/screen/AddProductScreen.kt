package com.example.legacyframeapp.ui.screen

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
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
import com.example.legacyframeapp.ui.components.AppButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.legacyframeapp.ui.components.ThousandSeparatorTransformation
import com.example.legacyframeapp.ui.components.formatWithThousands
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import java.io.File

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun AddProductScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val state by vm.addProduct.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Photo Picker (galería)
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        vm.onImageSelected(uri)
    }

    // Cámara: tomar foto y guardarla en un archivo temporal vía FileProvider
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val current = cameraUri
            vm.onImageSelected(current)
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

    // Permiso de cámara en tiempo de ejecución (debe ir después de declarar launchCamera)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
    }

    // Cuando 'saveSuccess' se vuelve true, resetea el estado y vuelve atrás
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
        onSelectImageGallery = {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCapturePhoto = {
            val hasCam = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (hasCam) launchCamera() else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        },
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
    name: String, description: String, price: String, imageUri: Uri?,
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                onValueChange = { onPriceChange(it.filter(Char::isDigit).take(6)) },
                label = { Text("Precio (CLP)") },
                placeholder = { Text("0") },
                isError = priceError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                visualTransformation = ThousandSeparatorTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (priceError != null) { Text(priceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
            else if (price.isNotBlank()) {
                Text("Precio: $ " + formatWithThousands(price), style = MaterialTheme.typography.labelSmall)
            }


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
                    Text("Guardar Producto")
                }
            }

            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
