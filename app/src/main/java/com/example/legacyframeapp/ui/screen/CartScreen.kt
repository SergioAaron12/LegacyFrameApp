package com.example.legacyframeapp.ui.screen
<<<<<<< HEAD

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
=======
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.rememberVectorPainter
=======
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
<<<<<<< HEAD
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Photo
import java.io.File
=======
import java.text.NumberFormat
import java.util.*
import android.Manifest // Para el permiso
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf

// --- Helper para formatear CLP (Peso Chileno) ---
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
    onNavigateBack: () -> Unit,
    onCheckout: () -> Unit = {}
) {
<<<<<<< HEAD
    val items by vm.cartItems.collectAsStateWithLifecycle()
    val total by vm.cartTotal.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Acción de checkout: compone mensaje, abre WhatsApp y limpia el carrito
    val doCheckout = {
        val lines = items.joinToString("\n") { "- ${it.name} x${it.quantity} = ${formatPrice(it.price * it.quantity)}" }
        val message = "Pedido Legacy Frames:\n$lines\nTotal: ${formatPrice(total)}"
        val url = "https://api.whatsapp.com/send?phone=56227916878&text=" + android.net.Uri.encode(message)
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        context.startActivity(intent)
        // Registrar compra en historial (si está disponible)
        vm.recordOrder(items, total)
        vm.clearCart()
    }

    CartScreen(
        items = items,
        total = total,
        onIncrement = { item -> vm.updateCartQuantity(item, item.quantity + 1) },
        onDecrement = { item ->
            val newQty = item.quantity - 1
            if (newQty <= 0) vm.removeFromCart(item) else vm.updateCartQuantity(item, newQty)
        },
        onRemove = { vm.removeFromCart(it) },
        onCheckout = doCheckout,
=======
    val cartItems by vm.cartItems.collectAsStateWithLifecycle()
    val cartTotal by vm.cartTotal.collectAsStateWithLifecycle()

    CartScreen(
        items = cartItems,
        total = cartTotal,
        onAddOne = { item -> vm.updateCartQuantity(item, item.quantity + 1) },
        onRemoveOne = { item -> vm.updateCartQuantity(item, item.quantity - 1) },
        onClearCart = vm::clearCart,

        // --- MODIFICAR ESTE BLOQUE ---
        onCheckout = { didGrantPermission ->
            // 1. Muestra la notificación SI TENEMOS PERMISO
            if (didGrantPermission) {
                vm.showPurchaseNotification()
            }
            // 2. (Siempre) Limpia el carrito y vuelve atrás
            vm.clearCart()
            onNavigateBack()
        },
        // ------------------------------

>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
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
<<<<<<< HEAD
    onIncrement: (CartItemEntity) -> Unit,
    onDecrement: (CartItemEntity) -> Unit,
    onRemove: (CartItemEntity) -> Unit,
    onCheckout: () -> Unit,
=======
    onAddOne: (CartItemEntity) -> Unit,
    onRemoveOne: (CartItemEntity) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: (didGrantPermission: Boolean) -> Unit,
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
    onBack: () -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Cuando el usuario responde (Sí o No),
            // llamamos a onCheckout pasando el resultado
            onCheckout(isGranted)
        }
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Carrito") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Color para el ícono de borrar
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
<<<<<<< HEAD
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
=======
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para vaciar el carrito
                    IconButton(onClick = onClearCart, enabled = items.isNotEmpty()) {
                        Icon(Icons.Default.Delete, contentDescription = "Vaciar carrito")
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
                    }
                }
            )
        },
<<<<<<< HEAD
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatPrice(total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onCheckout,
                    enabled = items.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Finalizar compra")
=======
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
                        onClick = {
                            // --- ACCIÓN MODIFICADA ---
                            // En lugar de llamar a onCheckout directamente,
                            // AHORA lanzamos el permiso.
                            // El resultado del lanzador (arriba) llamará a onCheckout.
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        enabled = items.isNotEmpty()
                    ) {
                        Text("Comprar")
                    }
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
                }
            }
        }
    ) { innerPadding ->

        // --- Contenido Principal (Lista de Items) ---

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
<<<<<<< HEAD
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío")
=======
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
<<<<<<< HEAD
                    .padding(innerPadding)
=======
                    .padding(innerPadding), // Padding del Scaffold
                contentPadding = PaddingValues(16.dp), // Padding interno
                verticalArrangement = Arrangement.spacedBy(12.dp)
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
            ) {
                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
<<<<<<< HEAD
                        onIncrement = { onIncrement(item) },
                        onDecrement = { onDecrement(item) },
                        onRemove = { onRemove(item) }
                    )
                    HorizontalDivider()
                }
                item { Spacer(modifier = Modifier.size(88.dp)) } // espacio para bottom bar
=======
                        onAddOne = { onAddOne(item) },
                        onRemoveOne = { onRemoveOne(item) }
                    )
                }
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
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
<<<<<<< HEAD
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current
    val placeholder = rememberVectorPainter(image = Icons.Default.Photo)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                val model: Any? = when {
                    item.imagePath.isNullOrBlank() -> null
                    item.imagePath.startsWith("http") -> item.imagePath
                    File(item.imagePath).exists() -> File(item.imagePath)
                    else -> {
                        val resId = context.resources.getIdentifier(item.imagePath, "drawable", context.packageName)
                        if (resId != 0) resId else null
                    }
                }
                AsyncImage(
                    model = model,
                    contentDescription = item.name,
                    placeholder = placeholder,
                    error = placeholder,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = formatPrice(item.price), style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
=======
    onAddOne: () -> Unit,
    onRemoveOne: () -> Unit
) {
    val context = LocalContext.current

    // --- LÓGICA DE IMAGEN (NUEVA) ---
    // Maneja tanto URLs (para "Cuadros") como archivos locales (para "Molduras")
    val imageModel = remember(item.imagePath) {
        when {
            // Si la ruta está vacía
            item.imagePath.isNullOrBlank() -> null
            // Si es una URL de internet
            item.imagePath.startsWith("http") -> item.imagePath
            // Si es un nombre de archivo local
            else -> ImageStorageHelper.getImageFile(context, item.imagePath)
        }
    }

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
                model = imageModel, // Usa el modelo de imagen inteligente
                contentDescription = item.name,
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
                Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(
                    formatCurrency(item.price * item.quantity), // Precio total del item
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
                // El `onRemoveOne` llamará a updateQuantity(item, qty - 1)
                // El repositorio se encargará de borrarlo si la cantidad es 0
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
                    fontWeight = FontWeight.Bold
                )

                // Botón Más (+)
                IconButton(onClick = onAddOne, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir uno")
                }
>>>>>>> b7b797d5722760f582b2ee745b1de7b6e4236fdf
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement) {
                    Text("-")
                }
                Text(text = item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onIncrement) {
                    Text("+")
                }
            }
            Text(text = "Subtotal: ${formatPrice(item.price * item.quantity)}")
        }
    }
}

private fun formatPrice(value: Int): String = "$" + String.format("%,d", value).replace(',', '.')
