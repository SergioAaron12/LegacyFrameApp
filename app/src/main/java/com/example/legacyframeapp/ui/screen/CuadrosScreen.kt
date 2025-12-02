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
import androidx.compose.material.icons.filled.AddShoppingCart
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
import com.example.legacyframeapp.R
import com.example.legacyframeapp.domain.model.Cuadro
import com.example.legacyframeapp.ui.components.formatWithThousands
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun CuadrosScreenVm(
    vm: AuthViewModel,
    onAddCuadro: () -> Unit
) {
    val cuadros by vm.cuadros.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    CuadrosScreen(
        cuadros = cuadros,
        isAdmin = session.isAdmin,
        onAddCuadro = onAddCuadro,
        onAddToCart = { cuadro -> vm.addCuadroToCart(cuadro) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuadrosScreen(
    cuadros: List<Cuadro>,
    isAdmin: Boolean,
    onAddCuadro: () -> Unit,
    onAddToCart: (Cuadro) -> Unit
) {
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (cuadros.isEmpty()) {
                item {
                    Text(
                        text = "No hay cuadros disponibles.",
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                items(cuadros, key = { it.id }) { cuadro ->
                    CuadroCard(
                        cuadro = cuadro,
                        onAddToCart = { onAddToCart(cuadro) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuadroCard(
    cuadro: Cuadro,
    onAddToCart: () -> Unit
) {
    val context = LocalContext.current
    val placeholderDrawable = R.drawable.ic_launcher_foreground

    // --- LÓGICA INTELIGENTE DE IMAGEN ---
    val model: Any? = remember(cuadro.imageUrl) {
        when {
            cuadro.imageUrl.startsWith("http") -> cuadro.imageUrl
            else -> {
                val resId = context.resources.getIdentifier(cuadro.imageUrl, "drawable", context.packageName)
                if (resId != 0) resId else null
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = model,
                contentDescription = cuadro.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                placeholder = null, // Opcional: placeholderDrawable
                error = null        // Opcional: placeholderDrawable
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cuadro.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val details = listOfNotNull(
                    cuadro.size.takeIf { it.isNotBlank() },
                    cuadro.material.takeIf { it.isNotBlank() }
                ).joinToString(" / ")

                if (details.isNotBlank()) {
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!cuadro.artist.isNullOrBlank()) {
                    Text(
                        text = "Autor: ${cuadro.artist}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = cuadro.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$ ${formatWithThousands(cuadro.price.toString())}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(onClick = onAddToCart, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Añadir al carrito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}