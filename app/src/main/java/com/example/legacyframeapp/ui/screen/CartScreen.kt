package com.example.legacyframeapp.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import java.text.NumberFormat
import java.util.*
import androidx.compose.material.icons.filled.Photo

// --- Helper para formatear CLP ---
private fun formatCurrency(amount: Int): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = 0
    }.format(amount)
}

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun CartScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    // Observamos los estados del carrito desde el ViewModel
    val cartItems by vm.cartItems.collectAsStateWithLifecycle()
    val cartTotal by vm.cartTotal.collectAsStateWithLifecycle()

    CartScreen(
        items = cartItems,
        total = cartTotal,
        onAddOne = vm::addOneToCart,     // Pasa la función de sumar 1
        onRemoveOne = vm::removeFromCart, // Pasa la función de restar 1
        onClearCart = vm::clearCart,    // Pasa la función de vaciar carrito
        onCheckout = {
            // TODO: (Paso Final) Aquí llamaremos a la Notificación
            println("Click en Comprar. Total: $cartTotal")
            vm.clearCart() // Vaciamos el carrito
            onNavigateBack() // Volvemos atrás
        },
        onBack = onNavigateBack
    )
}

// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    items: List<CartItemEntity>,
    total: Int,
    onAddOne: (CartItemEntity) -> Unit,
    onRemoveOne: (CartItemEntity) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Carrito") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para vaciar el carrito
                    IconButton(onClick = onClearCart, enabled = items.isNotEmpty()) {
                        Icon(Icons.Default.Delete, contentDescription = "Vaciar carrito")
                    }
                }
            )
        },
        // --- Barra inferior fija con el Total y el botón de Comprar ---
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total
                    Column {
                        Text("Total:", style = MaterialTheme.typography.labelMedium)
                        Text(
                            formatCurrency(total),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Botón Comprar
                    Button(
                        onClick = onCheckout,
                        enabled = items.isNotEmpty() // Solo se activa si hay items
                    ) {
                        Text("Comprar")
                    }
                }
            }
        }
    ) { innerPadding ->

        // --- Contenido Principal (Lista de Items) ---

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Padding del Scaffold
                contentPadding = PaddingValues(16.dp), // Padding interno
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onAddOne = { onAddOne(item) },
                        onRemoveOne = { onRemoveOne(item) }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 3. Fila para 1 item del carrito
// -----------------------------------------------------------------
@Composable
private fun CartItemRow(
    item: CartItemEntity,
    onAddOne: () -> Unit,
    onRemoveOne: () -> Unit
) {
    val context = LocalContext.current
    val imageFile = if (item.productImagePath.isNotBlank()) {
        ImageStorageHelper.getImageFile(context, item.productImagePath)
    } else null

    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Photo)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            AsyncImage(
                model = imageFile,
                contentDescription = item.productName,
                placeholder = placeholderPainter,
                error = placeholderPainter,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Textos (Nombre y Precio)
            Column(
                modifier = Modifier.weight(1f) // Ocupa el espacio del medio
            ) {
                Text(item.productName, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(
                    formatCurrency(item.productPrice * item.quantity), // Precio total del item
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.width(8.dp))

            // --- Controles de Cantidad ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón Menos (-)
                IconButton(onClick = onRemoveOne, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                        contentDescription = "Quitar uno"
                    )
                }

                // Cantidad
                Text(
                    item.quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp)
                )

                // Botón Más (+)
                IconButton(onClick = onAddOne, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir uno")
                }
            }
        }
    }
}