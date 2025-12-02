package com.example.legacyframeapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.R
import com.example.legacyframeapp.domain.model.Product
import com.example.legacyframeapp.ui.components.formatWithThousands
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.domain.ImageStorageHelper
import java.io.File

@Composable
fun MoldurasScreenVm(
    vm: AuthViewModel,
    onAddProduct: () -> Unit
) {
    val products by vm.products.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    MoldurasScreen(
        products = products,
        isAdmin = session.isAdmin,
        onAddProduct = onAddProduct,
        onAddToCart = vm::addProductToCart
    )
}

@Composable
fun MoldurasScreen(
    products: List<Product>,
    isAdmin: Boolean,
    onAddProduct: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    var fullscreenProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
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
                Text("No hay molduras disponibles.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        } else {
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

@Composable
private fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onImageClick: (Product) -> Unit
) {
    val context = LocalContext.current
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Photo)

    // --- LÓGICA INTELIGENTE DE IMAGEN ---
    val model: Any? = remember(product.imageUrl) {
        when {
            product.imageUrl.startsWith("http") -> product.imageUrl // URL de Internet
            else -> {
                // Nombre de recurso local (ej: "moldura1")
                val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
                if (resId != 0) resId else null
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                val (badgeBg, badgeFg) = categoryColors(product.category)
                AssistChip(
                    onClick = {},
                    label = { Text(product.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = badgeBg, labelColor = badgeFg)
                )
                Spacer(Modifier.height(4.dp))
                Text(product.description, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onImageClick(product) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Text("Ver", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
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
    return when (category.lowercase()) {
        "grecas" -> Color(0xFF8B5C2A) to Color.White
        "rústicas", "rusticas" -> Color(0xFF0DCAF0) to Color(0xFF07323A)
        "naturales" -> Color(0xFF198754) to Color.White
        "nativas" -> Color(0xFF6C757D) to Color.White
        "finger-joint", "fingerjoint", "finger_joint" -> Color(0xFFFFC107) to Color(0xFF3A2E00)
        else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
    }
}

@Composable
private fun FullscreenImageViewer(product: Product, onClose: () -> Unit) {
    val context = LocalContext.current
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Photo)

    val model: Any? = remember(product.imageUrl) {
        when {
            product.imageUrl.startsWith("http") -> product.imageUrl
            else -> {
                val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
                if (resId != 0) resId else null
            }
        }
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f))) {
        AsyncImage(
            model = model,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        if (scale > 1f) offset += pan else offset = Offset.Zero
                    }
                }
                .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y),
            contentScale = ContentScale.Fit,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp).align(Alignment.TopEnd), horizontalArrangement = Arrangement.End) {
            FloatingActionButton(onClick = onClose, containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) {
                Icon(Icons.Default.Add, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}