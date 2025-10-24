package com.example.legacyframeapp.ui.screen
import android.content.Intent
import android.net.Uri

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.data.local.cuadro.CuadroEntity
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun CuadrosScreenVm(
    vm: AuthViewModel,
    onAddCuadro: () -> Unit
) {
    val cuadros by vm.cuadros.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CuadrosScreen(
        cuadros = cuadros,
        isAdmin = session.isAdmin,
        onAddCuadro = onAddCuadro,
        onAddToCart = { cuadro -> vm.addCuadroToCart(cuadro) },
        onContactWhatsApp = { cuadro ->
            val msg = "Hola, me interesa el cuadro ${cuadro.title} (${cuadro.size}, ${cuadro.material}). ¿Podrían darme más información?"
            val url = "https://api.whatsapp.com/send?phone=56227916878&text=" + Uri.encode(msg)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CuadrosScreen(
    cuadros: List<CuadroEntity>,
    isAdmin: Boolean,
    onAddCuadro: () -> Unit,
    onAddToCart: (CuadroEntity) -> Unit,
    onContactWhatsApp: (CuadroEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // Filtrar cuadros por categoría seleccionada
    val filteredCuadros = if (selectedCategory == null) {
        cuadros
    } else {
        cuadros.filter { it.category == selectedCategory }
    }
    
    // Obtener categorías únicas
    val categories = cuadros.map { it.category }.distinct().sorted()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isAdmin,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onAddCuadro,
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Cuadro")
                }
            }
        }
    ) { innerPadding ->

        if (cuadros.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay cuadros disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cabecera
                item {
                    Text(
                        text = "Galería de Cuadros",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Filtros de categoría
                if (categories.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Categorías:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Chip "Todas"
                                FilterChip(
                                    onClick = { selectedCategory = null },
                                    label = { Text("Todas") },
                                    selected = selectedCategory == null
                                )
                                
                                // Chips de categorías
                                categories.forEach { category ->
                                    FilterChip(
                                        onClick = { 
                                            selectedCategory = if (selectedCategory == category) null else category 
                                        },
                                        label = { Text(category) },
                                        selected = selectedCategory == category
                                    )
                                }
                            }
                        }
                    }
                }

                // Lista de cuadros
                items(filteredCuadros, key = { it.id }) { cuadro ->
                    CuadroCard(
                        cuadro = cuadro,
                        onAddToCart = { onAddToCart(cuadro) },
                        onContactWhatsApp = { onContactWhatsApp(cuadro) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CuadroCard(
    cuadro: CuadroEntity,
    onAddToCart: () -> Unit,
    onContactWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del cuadro con Coil si hay imagePath; de lo contrario, placeholder
            if (cuadro.imagePath.isNotBlank()) {
                AsyncImage(
                    model = cuadro.imagePath,
                    contentDescription = cuadro.title,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Imagen del cuadro",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray
                )
            }

            Spacer(Modifier.width(16.dp))

            // Contenido principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cuadro.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = cuadro.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                
                Row {
                    Text(
                        text = "Tamaño: ${cuadro.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Material: ${cuadro.material}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                if (cuadro.artist != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Artista: ${cuadro.artist}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "$ ${cuadro.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onContactWhatsApp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Consultar", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    OutlinedButton(
                        onClick = onAddToCart,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart, 
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Carrito", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}