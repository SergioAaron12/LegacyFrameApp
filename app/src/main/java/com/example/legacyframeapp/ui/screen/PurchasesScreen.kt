package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import com.example.legacyframeapp.data.local.order.OrderEntity
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PurchasesScreenVm(
    vm: AuthViewModel,
    onBack: () -> Unit = {}
) {
    val orders by vm.orders.collectAsStateWithLifecycle()
    val products by vm.products.collectAsStateWithLifecycle()
    val cuadros by vm.cuadros.collectAsStateWithLifecycle()
    PurchasesScreen(orders = orders, products = products, cuadros = cuadros)
}

@Composable
fun PurchasesScreen(orders: List<OrderEntity>, products: List<ProductEntity>, cuadros: List<CuadroEntity>) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            Text("Tus compras", style = MaterialTheme.typography.headlineSmall)
            if (orders.isEmpty()) {
                Text("Aún no tienes compras registradas.")
            } else {
                LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                    items(orders, key = { it.id }) { order ->
                        OrderCard(order = order, products = products, cuadros = cuadros)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderEntity, products: List<ProductEntity>, cuadros: List<CuadroEntity>) {
    val parsed = remember(order.itemsText) { parseOrderItems(order.itemsText) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(order.dateMillis),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Total: ${formatPrice(order.total)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            parsed.forEach { item ->
                PurchaseItemRow(item = item, products = products, cuadros = cuadros)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PurchaseItemRow(
    item: ParsedOrderItem,
    products: List<ProductEntity>,
    cuadros: List<CuadroEntity>
) {
    val context = LocalContext.current
    val placeholderDrawable = R.drawable.ic_launcher_foreground

    val imagePath: String? = remember(item.name, products, cuadros) {
        val prod = products.firstOrNull { it.name.equals(item.name, ignoreCase = true) }
        val cua = cuadros.firstOrNull { it.title.equals(item.name, ignoreCase = true) }
        prod?.imagePath ?: cua?.imagePath
    }

    val imageRequest = remember(imagePath) {
        val dataToLoad: Any? = imagePath?.let { path ->
            when {
                path.isBlank() -> null
                path.startsWith("http", ignoreCase = true) -> path
                ImageStorageHelper.getImageFile(context, path).exists() -> ImageStorageHelper.getImageFile(context, path)
                else -> {
                    val resId = context.resources.getIdentifier(path, "drawable", context.packageName)
                    if (resId != 0) resId else null
                }
            }
        }
        ImageRequest.Builder(context)
            .data(dataToLoad)
            .placeholder(placeholderDrawable)
            .error(placeholderDrawable)
            .crossfade(true)
            .build()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = imageRequest,
            contentDescription = item.name,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Cantidad: ${item.quantity}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                item.priceText?.let { pt ->
                    Text(text = "Subtotal: $pt", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private data class ParsedOrderItem(val name: String, val quantity: Int, val priceText: String?)

private fun parseOrderItems(itemsText: String): List<ParsedOrderItem> {
    // Formato esperado por línea: "- Nombre x3 = $ 12.345"
    val lines = itemsText.lines().map { it.trim() }.filter { it.isNotBlank() }
    val regex = Regex("^-\\s*(.+?)\\s*x(\\d+)\\s*(?:=\\s*(.+))?$", RegexOption.IGNORE_CASE)
    return lines.mapNotNull { line ->
        val match = regex.find(line) ?: return@mapNotNull null
        val (name, qtyStr, priceText) = match.destructured
        val qty = qtyStr.toIntOrNull() ?: 1
        ParsedOrderItem(name.trim(), qty, priceText?.trim())
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatPrice(value: Int): String = "$" + String.format("%,d", value).replace(',', '.')
