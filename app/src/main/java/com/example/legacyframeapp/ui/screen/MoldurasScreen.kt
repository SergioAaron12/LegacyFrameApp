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

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// --- Material Icons ---

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.graphicsLayer

// --- ViewModel y Lifecycle ---

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.domain.model.Product // <--- CAMBIO IMPORTANTE
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun MoldurasScreenVm(
    vm: AuthViewModel,
    onAddProduct: () -> Unit
) {
    // Observamos los estados del ViewModel (que ahora es List<Product>)
    val products by vm.products.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    MoldurasScreen(
        products = products,
        isAdmin = session.isAdmin,
        onAddProduct = onAddProduct,
        onAddToCart = vm::addProductToCart
    )
}

// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@Composable
fun MoldurasScreen(
    products: List<Product>, // <--- CAMBIO: ProductEntity -> Product
    isAdmin: Boolean,
    onAddProduct: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    // Estado para el visor fullscreen (usando Product)
    var fullscreenProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        // --- Botón Flotante de Admin ---
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
    ) { innerPadding ->

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
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Catálogo de Molduras",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // items usa la key 'id' del Product
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { onAddToCart(product) },
                        onImageClick = { fullscreenProduct = product }
                    )
                }
            }
        }
    }

    // Visor fullscreen
    if (fullscreenProduct != null) {
        Dialog(
            onDismissRequest = { fullscreenProduct = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            FullscreenImageViewer(
                product = fullscreenProduct!!,
                onClose = { fullscreenProduct = null }
            )
        }
    }
}

// -----------------------------------------------------------------
// 3. Card para mostrar 1 producto
// -----------------------------------------------------------------
@Composable
private fun ProductCard(
    product: Product, // <--- CAMBIO: ProductEntity -> Product
    onAddToCart: () -> Unit,
    onImageClick: (Product) -> Unit
) {

    val context = LocalContext.current
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Photo)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Resolver modelo de imagen: URL, archivo o drawable
            // CAMBIO: imagePath -> imageUrl
            val model: Any? = when {
                product.imageUrl.isNotBlank() && product.imageUrl.startsWith("http") -> product.imageUrl
                product.imageUrl.isNotBlank() && java.io.File(product.imageUrl).exists() -> java.io.File(product.imageUrl)
                product.imageUrl.isNotBlank() -> {
                    val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
                    if (resId != 0) resId else ImageStorageHelper.getImageFile(context, product.imageUrl)
                }
                else -> null
            }
            AsyncImage(
                model = model,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = placeholderPainter
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))

                // Badge de categoría
                val (badgeBg, badgeFg) = categoryColors(product.category)
                AssistChip(
                    onClick = {},
                    // Formateo de texto de categoría
                    label = {
                        Text(
                            product.category.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            }
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = badgeBg,
                        labelColor = badgeFg
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val green = Color(0xFF2E7D32)
                    Button(
                        onClick = { onImageClick(product) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Ver", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = green,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Añadir al carrito",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Carrito", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun categoryColors(category: String): Pair<Color, Color> {
    // Paleta basada en Bootstrap usada por el sitio
    return when (category.lowercase()) {
        "grecas" -> Color(0xFF8B5C2A) to Color.White // primary
        "rústicas", "rusticas" -> Color(0xFF0DCAF0) to Color(0xFF07323A) // info
        "naturales" -> Color(0xFF198754) to Color.White // success
        "nativas" -> Color(0xFF6C757D) to Color.White // secondary
        "finger-joint", "fingerjoint", "finger_joint", "finger joint" -> Color(0xFFFFC107) to Color(0xFF3A2E00) // warning
        else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
    }
}

// ---------------------------------------------------------------
// Fullscreen viewer con zoom y desplazamiento
// ---------------------------------------------------------------
@Composable
private fun FullscreenImageViewer(
    product: Product, // <--- CAMBIO: ProductEntity -> Product
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Photo)

    // Modelo igual que en ProductCard. CAMBIO: imagePath -> imageUrl
    val model: Any? = when {
        product.imageUrl.isNotBlank() && product.imageUrl.startsWith("http") -> product.imageUrl
        product.imageUrl.isNotBlank() && java.io.File(product.imageUrl).exists() -> java.io.File(product.imageUrl)
        product.imageUrl.isNotBlank() -> {
            val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
            if (resId != 0) resId else ImageStorageHelper.getImageFile(context, product.imageUrl)
        }
        else -> null
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Reset cuando cambia el producto
    androidx.compose.runtime.LaunchedEffect(product.id) {
        scale = 1f
        offset = Offset.Zero
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        // Imagen con gestos de zoom y pan
        AsyncImage(
            model = model,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 5f)
                        scale = newScale
                        if (newScale > 1f) {
                            offset += pan
                        } else {
                            offset = Offset.Zero
                        }
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )
        // Barra superior de cierre
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.End
        ) {
            FloatingActionButton(
                onClick = onClose,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}