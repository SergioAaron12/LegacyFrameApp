package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.legacyframeapp.R
import com.example.legacyframeapp.domain.model.Product
import com.example.legacyframeapp.ui.components.formatWithThousands

data class ServiceHome(val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String, val description: String)

@Composable
fun HomeScreen(
    vm: com.example.legacyframeapp.ui.viewmodel.AuthViewModel,
    onNavigateToMolduras: () -> Unit,
    onNavigateToCuadros: () -> Unit // <--- NUEVO PARÁMETRO
) {
    val products by vm.products.collectAsState()
    val cuadros by vm.cuadros.collectAsState()

    val cuadrosAsProducts = cuadros.map { cuadro ->
        Product(
            id = cuadro.id,
            name = cuadro.title,
            description = cuadro.description,
            price = cuadro.price,
            imageUrl = cuadro.imageUrl,
            category = ""
        )
    }

    val popularProducts = (products.take(3) + cuadrosAsProducts.take(3)).shuffled()

    val services = listOf(
        ServiceHome(Icons.Default.Star, "Enmarcación Personalizada", "Creamos marcos a medida para cualquier obra."),
        ServiceHome(Icons.Default.LocalShipping, "Despacho a Domicilio", "Entregamos tus cuadros directamente en tu hogar."),
        ServiceHome(Icons.Default.Timer, "Servicio Express", "Enmarcación rápida en 24-48 horas.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 1. HEADER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Aumenté un poco la altura
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_header),
                    contentDescription = "Header",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Capa oscura para leer mejor el texto
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

                // --- ARREGLO VISUAL ---
                // Cambié Alignment.BottomStart a Alignment.Center
                // Así el texto queda en medio de la foto y la tarjeta blanca no lo tapa.
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido a Legacy Frames",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enmarcaciones de alta calidad",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 2. TARJETA DE BIENVENIDA
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-40).dp), // Sube sobre la imagen
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo "L"
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("L", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }

                    Text("LegacyFrame", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Tradición y calidad en enmarcación desde 1998",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToMolduras,
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(Icons.Default.Category, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Molduras")
                        }

                        // --- ARREGLO BOTÓN: Ahora usa la función onNavigateToCuadros ---
                        Button(
                            onClick = onNavigateToCuadros,
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(Icons.Default.Collections, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cuadros")
                        }
                    }
                }
            }
        }

        // 3. MÁS VENDIDOS
        item {
            Column(modifier = Modifier.offset(y = (-20).dp).padding(bottom = 20.dp)) {
                Text(
                    text = "Nuestros Más Vendidos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 16.dp)
                        .width(60.dp)
                        .height(4.dp)
                        .background(Color(0xFF8B5C2A), RoundedCornerShape(2.dp))
                )

                if (popularProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando...", color = Color.Gray)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(popularProducts) { product ->
                            ProductCardHome(product)
                        }
                    }
                }
            }
        }

        // 4. SERVICIOS
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Servicios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5C2A),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                services.forEach { service ->
                    ServiceItem(service)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp)) // Espacio final para que no lo tape el menú
        }
    }
}

@Composable
fun ProductCardHome(product: Product) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "$ ${'$'}{formatWithThousands(product.price.toString())}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF555555)
                )
            }
        }
    }
}

@Composable
fun ServiceItem(service: ServiceHome) {
    Surface(
        color = Color(0xFFEEE4DC),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = null,
                tint = Color(0xFF8B5C2A),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = service.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = service.description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }
    }
}