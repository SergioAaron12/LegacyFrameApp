package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.data.local.order.OrderEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PurchasesScreenVm(
    vm: AuthViewModel,
    onBack: () -> Unit = {}
) {
    val orders by vm.orders.collectAsStateWithLifecycle()
    PurchasesScreen(orders = orders)
}

@Composable
fun PurchasesScreen(orders: List<OrderEntity>) {
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
                        Text(formatDate(order.dateMillis), style = MaterialTheme.typography.titleMedium)
                        Text(order.itemsText)
                        Text("Total: ${formatPrice(order.total)}", style = MaterialTheme.typography.bodyMedium)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatPrice(value: Int): String = "$" + String.format("%,d", value).replace(',', '.')
