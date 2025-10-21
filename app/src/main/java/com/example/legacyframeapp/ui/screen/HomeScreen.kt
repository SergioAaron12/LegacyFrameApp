package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.legacyframeapp.R

// --- INSTRUCCIONES PARA IMÁGENES ---
// 1. Ve a la carpeta `res` en el explorador de archivos de Android Studio.
// 2. Haz clic derecho en la carpeta `drawable` -> New -> Image Asset, o simplemente arrastra tus imágenes ahí.
// 3. Reemplaza los `R.drawable.placeholder_...` en el código con los nombres de tus imágenes.

// Datos de ejemplo para las tarjetas (similares a los de tu web)
data class Product(val imageResId: Int, val title: String, val description: String)
data class Service(val icon: String, val title: String, val description: String)

@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    // Datos de ejemplo que simulan tu página web
    val popularProducts = listOf(
        Product(R.drawable.moldura1, "I 09 Greca ZO", "Diseño tradicional perfecto para fotografías familiares."),
        Product(R.drawable.moldura2, "Rústica de Campo", "Acabado natural que resalta la veta de la madera."),
        Product(R.drawable.moldura3, "Nativa Alerce", "Calidez y elegancia con madera nativa de primera.")
    )
    val services = listOf(
        Service("🖼️", "Enmarcación Personalizada", "Creamos marcos a medida para cualquier obra."),
        Service("🚚", "Despacho a Domicilio", "Entregamos tus cuadros directamente en tu hogar."),
        Service("⚡", "Servicio Express", "Enmarcación rápida en 24-48 horas.")
    )

    // LazyColumn permite que la pantalla sea desplazable (scroll)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5)) // Color de fondo claro de tu web
    ) {
        // --- 1. Banner Principal ---
        item {
            Box {
                Image(
                    // =====> AQUÍ VA TU IMAGEN DEL BANNER <=====
                    // Reemplaza 'placeholder_banner' por el nombre de tu imagen en res/drawable
                    painter = painterResource(id = R.drawable.legacy_frame_banner),
                    contentDescription = "Banner de Cuadros",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop // Esto asegura que la imagen cubra el espacio
                )
                // Contenedor de la Empresa (se superpone al banner)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 50.dp), // Efecto de superposición
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Legacy Frames",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5C2A) // Color primario
                        )
                        Text(
                            "Tradición y calidad en enmarcación desde 1998",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Espacio para compensar la superposición de la tarjeta
        item { Spacer(modifier = Modifier.height(74.dp)) }

        // --- 2. Productos Populares ---
        item {
            SectionTitle("Nuestros Productos Más Populares")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(popularProducts) { product ->
                    ProductCard(product)
                }
            }
        }

        // --- 3. Nuestros Servicios ---
        item {
            Spacer(modifier = Modifier.height(32.dp))
            SectionTitle("Nuestros Servicios")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                services.forEach { service ->
                    ServiceCard(service)
                }
            }
        }

        // --- 4. Call to Action (Llamado a la acción) ---
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8B5C2A)) // Color primario
                    .padding(vertical = 32.dp, horizontal = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "¿Listo para Enmarcar tu Historia?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = onGoLogin, // Puedes cambiarlo a una página de contacto
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF8B5C2A)
                            )
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contactar")
                        }
                        OutlinedButton(
                            onClick = onGoRegister,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Únete")
                        }
                    }
                }
            }
        }
    }
}

// Componente reutilizable para los títulos de sección
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C3A1A) // Color oscuro
        )
        Divider(
            modifier = Modifier
                .width(60.dp)
                .padding(top = 4.dp),
            thickness = 3.dp,
            color = Color(0xFFD4A574) // Color de acento
        )
    }
}

// Componente reutilizable para las tarjetas de productos
@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.width(180.dp), // Ancho fijo para las tarjetas en la fila
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Image(
                // =====> AQUÍ VA TU IMAGEN DE PRODUCTO <=====
                painter = painterResource(id = product.imageResId),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(12.dp)) {
                Text(product.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    color = Color.Gray
                )
            }
        }
    }
}

// Componente reutilizable para las tarjetas de servicios
@Composable
fun ServiceCard(service: Service) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EAE4)) // Un color crema claro
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(service.icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(service.title, fontWeight = FontWeight.Bold)
                Text(service.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

