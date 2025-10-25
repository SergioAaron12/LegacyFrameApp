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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import java.io.File

@Composable
fun CuadrosScreenVm(
    vm: AuthViewModel,
    onAddCuadro: () -> Unit
) {
    val cuadros by vm.cuadros.collectAsStateWithLifecycle()
    val categories by vm.cuadroCategories.collectAsStateWithLifecycle()
    val filter by vm.cuadroFilter.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()

    CuadrosScreen(
        cuadros = cuadros,
        categories = categories,
        selectedCategory = filter,
        onCategorySelect = vm::setCuadroFilter,
        isAdmin = session.isAdmin,
        onAddCuadro = onAddCuadro,
        // --- ¡LÍNEA CORREGIDA! ---
        // Llama a la función específica para "Cuadros"
        onAddToCart = vm::addCuadroToCart
    )
}

@OptIn(ExperimentalLayoutApi::class) // Necesario para FlowRow
@Composable
fun CuadrosScreen(
    cuadros: List<CuadroEntity>,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    isAdmin: Boolean,
    onAddCuadro: () -> Unit,
    onAddToCart: (CuadroEntity) -> Unit // <-- Asegúrate de que reciba la entidad
) {
    val context = LocalContext.current

    // ... (El Scaffold y el FloatingActionButton están bien) ...
    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = isAdmin, /* ... */) {
                FloatingActionButton(onClick = onAddCuadro, /* ... */) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Cuadro")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ... (El 'item' para los FilterChip está bien) ...

            // --- REVISIÓN DE 'items' ---
            items(cuadros, key = { it.id }) { cuadro ->
                CuadroCard(
                    cuadro = cuadro,
                    // --- ¡CORREGIDO! ---
                    // Pasa el 'cuadro' específico a la función
                    onAddToCart = { onAddToCart(cuadro) },
                    onContactWhatsApp = {
                        val text = Uri.encode("Hola, me interesa el cuadro: ${cuadro.title}")
                        val url = "https://api.whatsapp.com/send?phone=56227916878&text=$text"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
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
    val context = LocalContext.current
    val placeholder = rememberVectorPainter(image = Icons.Default.Photo)
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del cuadro con Coil resolviendo URL/archivo/recurso; placeholder en ausencia
            val model: Any? = when {
                cuadro.imagePath.isNotBlank() && cuadro.imagePath.startsWith("http") -> cuadro.imagePath
                cuadro.imagePath.isNotBlank() && File(cuadro.imagePath).exists() -> File(cuadro.imagePath)
                cuadro.imagePath.isNotBlank() -> {
                    val resId = context.resources.getIdentifier(cuadro.imagePath, "drawable", context.packageName)
                    if (resId != 0) resId else null
                }
                else -> null
            }
            AsyncImage(
                model = model,
                contentDescription = cuadro.title,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder
            )

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
                // Badge de categoría similar a Molduras
                val (badgeBg, badgeFg) = categoryColors(cuadro.category)
                AssistChip(
                    onClick = {},
                    label = { Text(cuadro.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = badgeBg,
                        labelColor = badgeFg
                    )
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

@Composable
private fun categoryColors(category: String): Pair<Color, Color> {
    return when (category.lowercase()) {
        "grecas" -> Color(0xFF8B5C2A) to Color.White // primary
        "rusticas" -> Color(0xFF0DCAF0) to Color(0xFF07323A) // info
        "naturales" -> Color(0xFF198754) to Color.White // success
        "nativas" -> Color(0xFF6C757D) to Color.White // secondary
        "finger-joint", "fingerjoint", "finger_joint" -> Color(0xFFFFC107) to Color(0xFF3A2E00) // warning
        else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
    }
}