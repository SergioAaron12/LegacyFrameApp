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
import com.example.legacyframeapp.domain.model.Product // <--- IMPORT NUEVO
import com.example.legacyframeapp.domain.ImageStorageHelper

@Composable
fun DeleteProductScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
){
    val context = LocalContext.current
    // Ahora 'products' es una lista de objetos 'Product' del dominio
    val products by vm.products.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    val filtered = remember(name, products) {
        val q = name.trim()
        if (q.isEmpty()) products else products.filter { it.name.contains(q, ignoreCase = true) }
    }

    DeleteProductScreen(
        name = name,
        onNameChange = { name = it },
        onDeleteByName = {
            val found = products.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
            if (found != null) {
                pendingDeleteId = found.id
            } else {
                Toast.makeText(context, "No se encontró el producto", Toast.LENGTH_SHORT).show()
            }
        },
        onClickDeleteItem = { productId -> pendingDeleteId = productId },
        products = filtered,
        onBack = onNavigateBack
    )

    // Confirmación de borrado
    val toDelete = pendingDeleteId
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            confirmButton = {
                Button(onClick = {
                    val p = products.firstOrNull { it.id == toDelete }
                    if (p != null) {
                        vm.deleteProduct(p)
                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    }
                    pendingDeleteId = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar este producto? Esta acción no se puede deshacer.") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteProductScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onDeleteByName: () -> Unit,
    onClickDeleteItem: (Long) -> Unit,
    products: List<Product>, // <--- CAMBIO: Usamos Product en vez de ProductEntity
    onBack: () -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eliminar producto") },
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
            Text("Busca el producto por nombre o elimínalo desde la lista.")
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onDeleteByName, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                Text("Eliminar definitivamente")
            }

            Spacer(Modifier.height(8.dp))
            Text("Productos", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products, key = { it.id }) { p ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        // Resolución de imagen (URL o Local)
                        // CAMBIO: imagePath -> imageUrl
                        val model: Any? = if (p.imageUrl.startsWith("http")) {
                            p.imageUrl
                        } else {
                            // Lógica para imágenes locales o placeholders
                            ImageRequest.Builder(LocalContext.current)
                                .data(p.imageUrl) // Coil maneja archivos si la ruta es válida
                                .crossfade(true)
                                .build()
                        }

                        AsyncImage(
                            model = model,
                            contentDescription = p.name,
                            modifier = Modifier.size(56.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("$" + p.price.toString(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onClickDeleteItem(p.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }
}