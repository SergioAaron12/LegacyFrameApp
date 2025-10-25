package com.example.legacyframeapp.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun ChangeProductImageScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
){
    val context = LocalContext.current
    val products by vm.products.collectAsStateWithLifecycle()

    var selectedId by remember { mutableLongStateOf(0L) }
    var selectedName by remember { mutableStateOf("") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && selectedId != 0L) {
            vm.updateProductImage(context, selectedId, uri) { ok, err ->
                if (ok) {
                    Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                } else {
                    Toast.makeText(context, err ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ChangeProductImageScreen(
        selectedName = selectedName,
        onSearchChange = { query ->
            selectedName = query
            val found = products.firstOrNull { it.name.equals(query, ignoreCase = true) }
            selectedId = found?.id ?: 0L
        },
        onPickImage = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeProductImageScreen(
    selectedName: String,
    onSearchChange: (String) -> Unit,
    onPickImage: () -> Unit,
    onBack: () -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cambiar imagen de producto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Busca el producto por nombre y selecciona su nueva imagen.", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(
                value = selectedName,
                onValueChange = onSearchChange,
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPickImage, enabled = selectedName.isNotBlank()) {
                    Text("Elegir imagen de galería")
                }
            }
        }
    }
}
