package com.example.legacyframeapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun DeleteCuadroScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
){
    val context = LocalContext.current
    val cuadros by vm.cuadros.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    val filtered = remember(name, cuadros) {
        val q = name.trim()
        if (q.isEmpty()) cuadros else cuadros.filter { it.title.contains(q, ignoreCase = true) }
    }

    DeleteCuadroScreen(
        name = name,
        onNameChange = { name = it },
        onDeleteByName = {
            val found = cuadros.firstOrNull { it.title.equals(name.trim(), ignoreCase = true) }
            if (found != null) {
                pendingDeleteId = found.id
            } else {
                Toast.makeText(context, "No se encontró el cuadro", Toast.LENGTH_SHORT).show()
            }
        },
        onClickDeleteItem = { cuadroId -> pendingDeleteId = cuadroId },
        cuadros = filtered,
        onBack = onNavigateBack
    )

    // Confirmación de borrado
    val toDelete = pendingDeleteId
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            confirmButton = {
                Button(onClick = {
                    val c = cuadros.firstOrNull { it.id == toDelete }
                    if (c != null) {
                        vm.deleteCuadro(c)
                        Toast.makeText(context, "Cuadro eliminado", Toast.LENGTH_SHORT).show()
                    }
                    pendingDeleteId = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar este cuadro? Esta acción no se puede deshacer.") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteCuadroScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onDeleteByName: () -> Unit,
    onClickDeleteItem: (Long) -> Unit,
    cuadros: List<com.example.legacyframeapp.data.local.cuadro.CuadroEntity>,
    onBack: () -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eliminar cuadro") },
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
            Text("Busca el cuadro por título o elimínalo desde la lista.")
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Título del cuadro") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onDeleteByName, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                Text("Eliminar definitivamente")
            }

            Spacer(Modifier.height(8.dp))
            Text("Cuadros", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cuadros, key = { it.id }) { c ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(c.imagePath)
                                .crossfade(true)
                                .build(),
                            contentDescription = c.title,
                            modifier = Modifier.size(56.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(c.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (c.artist != null) {
                                Text(c.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                            Text("$" + c.price.toString(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onClickDeleteItem(c.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }
}
