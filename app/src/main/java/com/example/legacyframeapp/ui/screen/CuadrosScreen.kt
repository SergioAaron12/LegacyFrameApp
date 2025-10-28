package com.example.legacyframeapp.ui.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart // Icono de carrito para IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import com.example.legacyframeapp.domain.ImageStorageHelper
import com.example.legacyframeapp.ui.components.formatWithThousands
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// --- Stateful Composable (Conecta con ViewModel) ---
@Composable
fun CuadrosScreenVm(
    vm: AuthViewModel,
    onAddCuadro: () -> Unit
) {
    // --- USA LA LISTA COMPLETA, SIN FILTRO ---
    val cuadros by vm.cuadros.collectAsStateWithLifecycle()
    // -----------------------------------------
    // val categories by vm.cuadroCategories.collectAsStateWithLifecycle() // Ya no se usa
    // val filter by vm.cuadroFilter.collectAsStateWithLifecycle() // Ya no se usa
    val session by vm.session.collectAsStateWithLifecycle()

    CuadrosScreen(
        cuadros = cuadros, // Pasa la lista completa
        // categories = categories, // Ya no se pasa
        // selectedCategory = filter, // Ya no se pasa
        // onCategorySelect = vm::setCuadroFilter, // Ya no se pasa
        isAdmin = session.isAdmin,
        onAddCuadro = onAddCuadro,
        onAddToCart = { cuadro -> vm.addCuadroToCart(cuadro) }
    )
}

// --- Stateless Composable (Solo UI) ---
@OptIn(ExperimentalMaterial3Api::class) // Quita ExperimentalLayoutApi si no usas FlowRow
@Composable
fun CuadrosScreen(
    cuadros: List<CuadroEntity>,
    // Quita parámetros de categorías y filtro
    // categories: List<String>,
    // selectedCategory: String,
    // onCategorySelect: (String) -> Unit,
    isAdmin: Boolean,
    onAddCuadro: () -> Unit,
    onAddToCart: (CuadroEntity) -> Unit
) {
    // val context = LocalContext.current // No se usa directamente aquí ahora
    // val green = Color(0xFF2E7D32) // No se usa directamente aquí ahora

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isAdmin,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(onClick = onAddCuadro) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Cuadro")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp), // Padding inferior por FAB
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre tarjetas
        ) {
            // --- QUITA LOS FILTROS DE CATEGORÍA ---
            // item {
            //     FlowRow(...) { ... FilterChip ... }
            // }
            // --------------------------------------

            // --- Lista de Cuadros ---
            if (cuadros.isEmpty()) {
                item {
                    Text(
                        text = "No hay cuadros disponibles.", // Mensaje simplificado
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                items(cuadros, key = { it.id }) { cuadro ->
                    // --- USA EL CuadroCard MODIFICADO ---
                    CuadroCard(
                        cuadro = cuadro,
                        onAddToCart = { onAddToCart(cuadro) }
                        // Quita onContactWhatsApp
                    )
                    // ------------------------------------
                }
            }
        }
    }
}

// --- Tarjeta para mostrar un Cuadro (MODIFICADA para ser idéntica a ProductCard) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuadroCard(
    cuadro: CuadroEntity,
    onAddToCart: () -> Unit
    // Quita onContactWhatsApp
) {
    val context = LocalContext.current
    val placeholderDrawable = R.drawable.ic_launcher_foreground // O tu placeholder

    // Lógica de carga de imagen (sin cambios)
    val imageRequest = remember(cuadro.imagePath) {
        val dataToLoad: Any? = when {
            cuadro.imagePath.isBlank() -> null
            cuadro.imagePath.startsWith("http", ignoreCase = true) -> cuadro.imagePath
            cuadro.imagePath.contains("_") && ImageStorageHelper.getImageFile(context, cuadro.imagePath).exists() ->
                ImageStorageHelper.getImageFile(context, cuadro.imagePath)
            else -> {
                val resourceId = context.resources.getIdentifier(cuadro.imagePath, "drawable", context.packageName)
                if (resourceId != 0) resourceId else null
            }
        }
        ImageRequest.Builder(context)
            .data(dataToLoad).placeholder(placeholderDrawable).error(placeholderDrawable).crossfade(true).build()
    }

    // --- DISEÑO IDÉNTICO A ProductCard ---
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen a la izquierda
            AsyncImage(
                model = imageRequest,
                contentDescription = cuadro.title,
                modifier = Modifier
                    .size(90.dp) // Tamaño como en ProductCard
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            // Columna para el texto y botón carrito
            Column(
                modifier = Modifier.weight(1f) // Ocupa el espacio restante
            ) {
                Text(
                    text = cuadro.title,
                    style = MaterialTheme.typography.titleMedium, // Mismo estilo que ProductCard
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Muestra tamaño y material (si existen)
                Text(
                    text = listOfNotNull(
                        cuadro.size.takeIf { it.isNotBlank() },
                        cuadro.material.takeIf { it.isNotBlank() }
                    ).joinToString(" / "),
                    style = MaterialTheme.typography.bodySmall, // Estilo pequeño
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // NO mostramos artista para igualar a ProductCard
                // if (cuadro.artist != null) { ... }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = cuadro.description,
                    style = MaterialTheme.typography.bodySmall, // Mismo estilo que ProductCard
                    maxLines = 2, // Limita a 2 líneas como ProductCard
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))

                // Fila inferior SOLO para Precio y Botón Carrito
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Separa precio y botón
                ) {
                    // Precio
                    Text(
                        text = "$ ${formatWithThousands(cuadro.price.toString())}",
                        style = MaterialTheme.typography.bodyLarge, // Mismo estilo que ProductCard
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Botón Añadir al Carrito (IconButton como en ProductCard)
                    IconButton(onClick = onAddToCart, modifier = Modifier.size(36.dp)) { // Mismo tamaño
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart, // Mismo icono
                            contentDescription = "Añadir al carrito",
                            tint = MaterialTheme.colorScheme.primary // Mismo tinte
                        )
                    }
                    // QUITA el botón de WhatsApp
                }
            }
        }
    }
    // ---------------------------------
}