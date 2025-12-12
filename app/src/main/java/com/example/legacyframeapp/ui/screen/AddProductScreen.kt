package com.example.legacyframeapp.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    vm: AuthViewModel,
    onBack: () -> Unit
) {
    val state by vm.addProduct.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    // Mapeo manual de categorías (Según DataLoader del backend)
    // 2=Grecas, 3=Rústicas, 4=Nativas, 5=Finger-Joint, 6=Naturales
    val categories = listOf(
        2L to "Grecas",
        3L to "Rústicas",
        4L to "Nativas",
        5L to "Finger-Joint",
        6L to "Naturales"
    )
    val selectedCategoryName = categories.find { it.first == state.categoryId }?.second ?: "Seleccionar"

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri != null) vm.onImageSelected(tempPhotoUri)
    }
    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.onImageSelected(uri)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            try {
                tempPhotoUri = vm.createTempImageUri()
                cameraLauncher.launch(tempPhotoUri!!)
            } catch (e: Exception) { Toast.makeText(context, "Error cámara", Toast.LENGTH_SHORT).show() }
        } else Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Moldura") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Datos de la moldura", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // FOTO
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUri != null) AsyncImage(model = state.imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray); Text("Toca para imagen", color = Color.Gray) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { try { permissionLauncher.launch(android.Manifest.permission.CAMERA) } catch (e: Exception) {} }, modifier = Modifier.weight(1f)) { Text("Cámara") }
                Button(onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Galería") }
            }

            // CAMPOS
            OutlinedTextField(value = state.name, onValueChange = { vm.onAddProductChange(name = it) }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())

            // --- SELECTOR DE CATEGORÍA ---
            Box {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    label = { Text("Tipo de Moldura") },
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                    enabled = false, // Para que el clic lo maneje la Box
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                // Capa transparente clickable para abrir el menú
                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                vm.onAddProductCategoryChange(id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = state.description, onValueChange = { vm.onAddProductChange(description = it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = state.price, onValueChange = { vm.onAddProductChange(price = it) }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = { vm.saveProduct(context) }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = state.canSubmit && !state.isSaving) {
                if (state.isSaving) CircularProgressIndicator(color = Color.White) else Text("Guardar")
            }

            if (state.saveSuccess) { vm.clearAddProductState(); onBack() }
        }
    }
}