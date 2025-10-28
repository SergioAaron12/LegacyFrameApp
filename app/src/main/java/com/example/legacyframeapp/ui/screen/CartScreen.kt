package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCartCheckout
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
import coil.request.ImageRequest
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.ui.components.formatWithThousands
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow

// Pantalla de Carrito (con ViewModel): obtiene items y total, maneja acciones de compra
@Composable
fun CartScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val items by vm.cartItems.collectAsStateWithLifecycle()
    val total by vm.cartTotal.collectAsStateWithLifecycle()

    CartScreen(
        items = items,
        total = total,
        onRemoveOne = { item -> vm.updateCartQuantity(item, item.quantity - 1) },
        onAddOne = { item -> vm.updateCartQuantity(item, item.quantity + 1) },
        onRemoveItem = vm::removeFromCart,
        onPurchase = {
            vm.recordOrder(items, total)
            vm.showPurchaseNotification()
            vm.clearCart()
            onNavigateBack()
        },
        onBack = onNavigateBack
    )
}

// UI del Carrito: barra superior, lista de items y barra inferior con total y botón Comprar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    items: List<CartItemEntity>,
    total: Int,
    onRemoveOne: (CartItemEntity) -> Unit,
    onAddOne: (CartItemEntity) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onPurchase: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                // Total formateado con separador de miles
                                text = "$ ${formatWithThousands(total.toString())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(onClick = onPurchase) {
                            Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Comprar")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box( /* ... Mensaje Carrito Vacío ... */ ) {
                Text("Tu carrito está vacío.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    // Fila de item con controles de cantidad y eliminar
                    CartItemRow(
                        item = item,
                        onRemoveOne = { onRemoveOne(item) },
                        onAddOne = { onAddOne(item) },
                        onRemoveItem = { onRemoveItem(item) }
                    )
                    HorizontalDivider() // Separador
                }
            }
        }
    }
}

// Fila de un item del carrito: imagen, nombre, precio, controles +/- y subtotal
@Composable
fun CartItemRow(
    item: CartItemEntity,
    onRemoveOne: () -> Unit,
    onAddOne: () -> Unit,
    onRemoveItem: () -> Unit
) {
    val context = LocalContext.current
    val placeholderDrawable = R.drawable.ic_launcher_foreground // O tu placeholder

    // Resolución de imagen: URL, archivo local o recurso drawable
    val imageRequest = remember(item.imagePath) {
        val dataToLoad: Any? = item.imagePath?.let { path ->
            when {
                path.isBlank() -> null
                path.startsWith("http", ignoreCase = true) -> path
                ImageStorageHelper.getImageFile(context, path).exists() ->
                    ImageStorageHelper.getImageFile(context, path)
                else -> {
                    val resourceId = context.resources.getIdentifier(path, "drawable", context.packageName)
                    if (resourceId != 0) resourceId else null
                }
            }
        }
        ImageRequest.Builder(context)
            .data(dataToLoad).placeholder(placeholderDrawable).error(placeholderDrawable).crossfade(true).build()
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
    // Fila superior: miniatura, nombre, precio unitario y botón eliminar
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = item.name,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = item.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        // Precio unitario formateado
                        text = "$ ${formatWithThousands(item.price.toString())}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            IconButton(onClick = onRemoveItem, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar del carrito", tint = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(Modifier.height(8.dp))

    // Fila inferior: controles de cantidad y subtotal calculado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Controles +/-/Cantidad
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = onRemoveOne, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) { Text("-") }
                Text(item.quantity.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                OutlinedButton(onClick = onAddOne, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) { Text("+") }
            }
            // Subtotal del item = precio * cantidad
            Text(
                text = "Subtotal: $ ${formatWithThousands((item.price * item.quantity).toString())}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}