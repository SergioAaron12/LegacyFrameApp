package com.example.legacyframeapp.ui.screen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Photo
import java.io.File

@Composable
fun CartScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit,
    onCheckout: () -> Unit = {}
) {
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
        onBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    items: List<CartItemEntity>,
    total: Int,
    onIncrement: (CartItemEntity) -> Unit,
    onDecrement: (CartItemEntity) -> Unit,
    onRemove: (CartItemEntity) -> Unit,
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
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
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { onIncrement(item) },
                        onDecrement = { onDecrement(item) },
                        onRemove = { onRemove(item) }
                    )
                    HorizontalDivider()
                }
                item { Spacer(modifier = Modifier.size(88.dp)) } // espacio para bottom bar
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
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
