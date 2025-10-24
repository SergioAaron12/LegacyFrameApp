package com.example.legacyframeapp.ui.screen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Photo // Icono placeholder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun MoldurasScreenVm(
    vm: AuthViewModel,
    onAddProduct: () -> Unit
) {
    // Observamos los estados del ViewModel
    val products by vm.products.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    MoldurasScreen(
        products = products,
        isAdmin = session.isAdmin,
        onAddProduct = onAddProduct
    )
}

// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@Composable
fun MoldurasScreen(
    products: List<ProductEntity>,
    isAdmin: Boolean,
    onAddProduct: () -> Unit
) {
    Scaffold(
        // --- Animación #2: Botón Flotante de Admin ---
        floatingActionButton = {
            AnimatedVisibility(
                visible = isAdmin,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onAddProduct,
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Moldura")
                }
            }
        }
    ) { innerPadding -> // 'innerPadding' es el espacio seguro (lejos de la TopBar, etc)

        // Verificamos si la lista está vacía
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay molduras disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            // --- Lista de Productos ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Aplicamos el padding del Scaffold
                contentPadding = PaddingValues(16.dp), // Padding interno para la lista
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre ítems
            ) {
                // Cabecera de la lista
                item {
                    Text(
                        text = "Catálogo de Molduras",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Un ítem por cada producto en la lista
                items(products, key = { it.id }) { product ->
                    ProductCard(product = product)
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 3. Card para mostrar 1 producto
// -----------------------------------------------------------------
@Composable
private fun ProductCard(product: ProductEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Placeholder de Imagen ---
            // (Más adelante, aquí cargaremos la foto desde product.imagePath con Coil)
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = "Placeholder de moldura",
                modifier = Modifier.size(80.dp),
                tint = Color.Gray
            )
            // -----------------------------

            Spacer(Modifier.width(16.dp))

            // --- Columna de Textos ---
            Column(
                modifier = Modifier.weight(1f) // Ocupa el espacio restante
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "$ ${product.price}", // Formato de precio simple
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
