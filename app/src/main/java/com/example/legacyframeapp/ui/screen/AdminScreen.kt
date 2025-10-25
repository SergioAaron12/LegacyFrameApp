package com.example.legacyframeapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun AdminScreenVm(
    vm: AuthViewModel,
    onGoAddProduct: () -> Unit,
    onGoChangeImage: () -> Unit,
    onGoDeleteProduct: () -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    AdminScreen(
        onAddProduct = onGoAddProduct,
        onChangeImage = onGoChangeImage,
        onDeleteProduct = onGoDeleteProduct,
        onPrefetchImages = {
            vm.prefetchProductImages(ctx)
            Toast.makeText(ctx, "Prefetch de imágenes iniciado", Toast.LENGTH_SHORT).show()
        },
        onClearCart = { vm.clearCart() },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onAddProduct: () -> Unit,
    onChangeImage: () -> Unit,
    onDeleteProduct: () -> Unit,
    onPrefetchImages: () -> Unit,
    onClearCart: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel de Administrador") },
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
            Text("Acciones rápidas", style = MaterialTheme.typography.titleMedium)

            Button(onClick = onAddProduct, modifier = Modifier.fillMaxWidth()) {
                Text("Añadir Moldura")
            }

            Button(onClick = onChangeImage, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar imagen de producto")
            }

            Button(onClick = onDeleteProduct, modifier = Modifier.fillMaxWidth()) {
                Text("Eliminar producto")
            }

            Button(onClick = onPrefetchImages, modifier = Modifier.fillMaxWidth()) {
                Text("Prefetch de Imágenes de Productos")
            }

            Button(onClick = onClearCart, modifier = Modifier.fillMaxWidth()) {
                Text("Vaciar Carrito")
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "Nota: más herramientas de administración pueden añadirse aquí (gestión de productos/cuadros, reportes, etc.)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
