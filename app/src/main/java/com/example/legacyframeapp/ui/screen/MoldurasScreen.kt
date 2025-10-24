package com.example.legacyframeapp.ui.screen
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.io.File

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
    val context = LocalContext.current

    MoldurasScreen(
        products = products,
        isAdmin = session.isAdmin,
        onAddProduct = onAddProduct,
        onAddToCart = { p -> vm.addProductToCart(p) },
        onDelete = { p -> vm.deleteProduct(p) },
        onContactWhatsApp = { p ->
            val msg = "Hola, me interesa la moldura ${'$'}{p.name}. ¿Podrían darme más información?"
            val url = "https://api.whatsapp.com/send?phone=56227916878&text=" + Uri.encode(msg)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    )
}

// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoldurasScreen(
    products: List<ProductEntity>,
    isAdmin: Boolean,
    onAddProduct: () -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onDelete: (ProductEntity) -> Unit,
    onContactWhatsApp: (ProductEntity) -> Unit
) {
    // Categorías fijas basadas en la web
    val categories = listOf("all", "grecas", "rusticas", "naturales", "nativas", "finger-joint")
    var selectedCategory by remember { mutableStateOf("all") }

    val filtered = if (selectedCategory == "all") products else products.filter { it.category == selectedCategory }
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

                // Filtros de categoría (chips)
                item {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(text = "Filtrar por categoría:", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categories.forEach { cat ->
                                val label = when (cat) {
                                    "all" -> "Todas"
                                    "grecas" -> "Grecas"
                                    "rusticas" -> "Rústicas"
                                    "naturales" -> "Naturales"
                                    "nativas" -> "Nativas"
                                    "finger-joint" -> "Finger Joint"
                                    else -> cat
                                }
                                FilterChip(
                                    onClick = { selectedCategory = cat },
                                    label = { Text(label) },
                                    selected = selectedCategory == cat
                                )
                            }
                        }
                    }
                }

                // Un ítem por cada producto en la lista
                items(filtered, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { onAddToCart(product) },
                        onContactWhatsApp = { onContactWhatsApp(product) },
                        onDelete = if (isAdmin) { { onDelete(product) } } else null
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 3. Card para mostrar 1 producto
// -----------------------------------------------------------------
@Composable
private fun ProductCard(
    product: ProductEntity,
    onAddToCart: () -> Unit,
    onContactWhatsApp: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto con Coil o drawable local; si no hay, placeholder
            val resId = when (product.imagePath) {
                "moldura1" -> com.example.legacyframeapp.R.drawable.moldura1
                "moldura2" -> com.example.legacyframeapp.R.drawable.moldura2
                "moldura3" -> com.example.legacyframeapp.R.drawable.moldura3
                else -> null
            }
            when {
                resId != null -> {
                    AsyncImage(
                        model = resId,
                        contentDescription = product.name,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                product.imagePath.startsWith("http") -> {
                    AsyncImage(
                        model = product.imagePath,
                        contentDescription = product.name,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    // Intenta cargar desde archivo local si existe
                    val f = File(product.imagePath)
                    if (f.exists()) {
                        AsyncImage(
                            model = f,
                            contentDescription = product.name,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Placeholder de moldura",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    }
                }
            }

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

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onContactWhatsApp, modifier = Modifier.weight(1f)) {
                        Text("Consultar")
                    }
                    OutlinedButton(onClick = onAddToCart, modifier = Modifier.weight(1f)) {
                        Text("Agregar")
                    }
                    if (onDelete != null) {
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}
