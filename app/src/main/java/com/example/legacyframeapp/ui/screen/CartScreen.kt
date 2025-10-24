package com.example.legacyframeapp.ui.screen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.data.local.cart.CartItemEntity
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun CartScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit
) {

    CartScreen(
        onBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    items: List<CartItemEntity>,
    total: Int,
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
                }
            )
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                contentAlignment = Alignment.Center
            ) {
            }
        } else {
                modifier = Modifier
                    .fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                        )
                    }
                }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemEntity,
) {
    Row(
        modifier = Modifier
    ) {
        }
            }
        }
    }
}