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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// Pantalla de Carrito (Conectada al ViewModel)
@Composable
fun CartScreenVm(
    vm: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRequireLogin: () -> Unit = {}
) {
    val items by vm.cartItems.collectAsStateWithLifecycle()
    val total by vm.cartTotal.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    // --- NUEVO: Obtenemos el valor del dólar desde la API externa ---
    val dolarValue by vm.dolarValue.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    CartScreen(
        items = items,
        total = total,
        dolarValue = dolarValue, // Pasamos el valor del dólar
        isLoggedIn = session.isLoggedIn,
        snackbarHostState = snackbarHostState,
        onRemoveOne = { item -> vm.updateCartQuantity(item, item.quantity - 1) },
        onAddOne = { item -> vm.updateCartQuantity(item, item.quantity + 1) },
        onRemoveItem = vm::removeFromCart,
        onPurchase = {
            if (!session.isLoggedIn) {
                scope.launch { snackbarHostState.showSnackbar("Debes iniciar sesión para comprar.") }
                onRequireLogin()
            } else {
                vm.recordOrder(items, total)
                vm.showPurchaseNotification()
                // vm.clearCart() // Esto ya se hace dentro de recordOrder si es exitoso
                onNavigateBack()
            }
        },
        onBack = onNavigateBack
    )
}

// UI del Carrito
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    items: List<CartItemEntity>,
    total: Int,
    dolarValue: Double?, // Valor del dólar (puede ser nulo si falla la API)
    isLoggedIn: Boolean,
    snackbarHostState: SnackbarHostState,
    onRemoveOne: (CartItemEntity) -> Unit,
    onAddOne: (CartItemEntity) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onPurchase: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                Surface(shadowElevation = 16.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // Más padding para que se vea bien
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total:", style = MaterialTheme.typography.bodyMedium)

                            // Total en Pesos
                            Text(
                                text = "$ ${formatWithThousands(total.toString())}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // --- NUEVO: Total en Dólares (API Externa) ---
                            if (dolarValue != null && dolarValue > 0) {
                                val totalUsd = total / dolarValue
                                val format = DecimalFormat("#,##0.00")
                                Text(
                                    text = "≈ US$ ${format.format(totalUsd)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Button(
                            onClick = onPurchase,
                            enabled = isLoggedIn,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (isLoggedIn) "Pagar" else "Ingresa")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Tu carrito está vacío.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onRemoveOne = { onRemoveOne(item) },
                        onAddOne = { onAddOne(item) },
                        onRemoveItem = { onRemoveItem(item) }
                    )
                    if (items.last() != item) {
                        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemEntity,
    onRemoveOne: () -> Unit,
    onAddOne: () -> Unit,
    onRemoveItem: () -> Unit
) {
    val context = LocalContext.current
    val placeholderDrawable = R.drawable.ic_launcher_foreground

    // Lógica robusta para imagen (URL vs Local)
    val imageRequest = remember(item.imagePath) {
        val dataToLoad: Any? = item.imagePath?.let { path ->
            when {
                path.isBlank() -> null
                path.startsWith("http", ignoreCase = true) -> path // Es URL (API)
                else -> {
                    // Es archivo local o drawable
                    val file = ImageStorageHelper.getImageFile(context, path)
                    if (file.exists()) {
                        file
                    } else {
                        val resId = context.resources.getIdentifier(path, "drawable", context.packageName)
                        if (resId != 0) resId else null
                    }
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

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = item.name,
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$ ${formatWithThousands(item.price.toString())}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            IconButton(onClick = onRemoveItem) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cantidad:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 8.dp))
            OutlinedButton(
                onClick = onRemoveOne,
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text("-") }

            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            OutlinedButton(
                onClick = onAddOne,
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text("+") }
        }
    }
}