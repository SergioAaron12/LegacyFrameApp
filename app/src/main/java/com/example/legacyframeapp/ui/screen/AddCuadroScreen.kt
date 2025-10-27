package com.example.legacyframeapp.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
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
import com.example.legacyframeapp.ui.components.AppButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
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

@Composable
fun AddCuadroScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val state by vm.addCuadro.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        vm.onCuadroImageSelected(uri)
    }

    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            vm.onCuadroImageSelected(cameraUri)
        }
    }

    fun launchCamera() {
        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
        val photoFile = File(imagesDir, "cuadro_${System.currentTimeMillis()}.jpg")
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

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            vm.clearAddCuadroState()
            onNavigateBack()
        }
    }

    AddCuadroScreen(
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
        onTitleChange = { vm.onAddCuadroChange(title = it) },
        onDescriptionChange = { vm.onAddCuadroChange(description = it) },
        onPriceChange = { vm.onAddCuadroChange(price = it) },
        onSizeChange = { vm.onAddCuadroChange(size = it) },
        onMaterialChange = { vm.onAddCuadroChange(material = it) },
        onCategoryChange = { vm.onAddCuadroChange(category = it) },
        onSelectImageGallery = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onCapturePhoto = {
            val hasCam = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (hasCam) launchCamera() else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        },
        onSubmit = { vm.saveCuadro(context) },
        onBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCuadroScreen(
    title: String,
    description: String,
    price: String,
    size: String,
    material: String,
    category: String,
    imageUri: Uri?,
    titleError: String?,
    priceError: String?,
    imageError: String?,
    isSaving: Boolean,
    canSubmit: Boolean,
    errorMsg: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onMaterialChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSelectImageGallery: () -> Unit,
    onCapturePhoto: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Cuadro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título") },
                isError = titleError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            if (titleError != null) {
                Text(titleError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción (Opcional)") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { onPriceChange(it.filter(Char::isDigit).take(6)) },
                label = { Text("Precio (CLP)") },
                placeholder = { Text("0") },
                isError = priceError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                visualTransformation = ThousandSeparatorTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (priceError != null) {
                Text(priceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            } else if (price.isNotBlank()) {
                Text("Precio: $ " + formatWithThousands(price), style = MaterialTheme.typography.labelSmall)
            }

            OutlinedTextField(
                value = size,
                onValueChange = onSizeChange,
                label = { Text("Tamaño (ej: 30x40 cm)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = material,
                onValueChange = onMaterialChange,
                label = { Text("Material") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = onCategoryChange,
                label = { Text("Categoría") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            RowWithImageButtons(onSelectImageGallery, onCapturePhoto)

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

            if (errorMsg != null) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// Helpers movidos a ui/components/PriceVisuals.kt

@Composable
private fun RowWithImageButtons(onSelectImageGallery: () -> Unit, onCapturePhoto: () -> Unit) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = onSelectImageGallery, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Galería")
        }
        OutlinedButton(onClick = onCapturePhoto, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Cámara")
        }
    }
}
