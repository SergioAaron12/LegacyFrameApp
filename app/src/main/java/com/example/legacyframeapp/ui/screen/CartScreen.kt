package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.ui.components.formatWithThousands
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// --- CORRECCIÓN: Ahora recibe el VM y saca los datos de ahí ---
@Composable
fun CartScreen(
    vm: AuthViewModel,
    onGoToPay: () -> Unit
) {
    val items by vm.cartItems.collectAsStateWithLifecycle()
    val total by vm.cartTotal.collectAsStateWithLifecycle()
    val dolarValue by vm.dolarValue.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    // Snackbar para mensajes
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }

    CartContent(
        items = items,
        total = total,
        dolarValue = dolarValue,
        isLoggedIn = session.isLoggedIn,
        onAddOne = { item -> vm.updateCartQuantity(item, item.quantity + 1) },
        onRemoveOne = { item ->
            if (item.quantity > 1) vm.updateCartQuantity(item, item.quantity - 1)
            else vm.removeFromCart(item)
        },
        onRemoveItem = { item -> vm.removeFromCart(item) },
        onPurchase = onGoToPay,
        onBack = { /* Opcional */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartContent(
    items: List<CartItemEntity>,
    total: Int,
    dolarValue: Double?,
    isLoggedIn: Boolean,
    onAddOne: (CartItemEntity) -> Unit,
    onRemoveOne: (CartItemEntity) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onPurchase: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Carrito") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        CartItemRow(item, onAddOne, onRemoveOne, onRemoveItem)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Resumen de Totales
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total (CLP):", style = MaterialTheme.typography.titleMedium)
                    Text("$ ${formatWithThousands(total.toString())}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                if (dolarValue != null) {
                    val totalUsd = total / dolarValue
                    Text(
                        text = "Aprox: US$ ${String.format("%.2f", totalUsd)} (Dólar: $dolarValue)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onPurchase,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = isLoggedIn && items.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(if (isLoggedIn) "Finalizar Compra" else "Inicia sesión para comprar")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemEntity,
    onAdd: (CartItemEntity) -> Unit,
    onRemove: (CartItemEntity) -> Unit,
    onDelete: (CartItemEntity) -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("$ ${formatWithThousands(item.price.toString())}", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onRemove(item) }) { Text("-", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) }
                Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onAdd(item) }) { Text("+", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) }
                IconButton(onClick = { onDelete(item) }) { Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red) }
            }
        }
    }
}