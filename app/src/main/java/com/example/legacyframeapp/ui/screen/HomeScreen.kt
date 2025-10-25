package com.example.legacyframeapp.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Category
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
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.ui.components.AppButton
import coil.compose.AsyncImage
import java.io.File



// Data classes para simular los datos de tu web
data class ProductHome(val title: String, val price: Int, val imageResId: Int? = null, val imagePath: String? = null)
data class ServiceHome(val icon: String, val title: String, val description: String)

@Composable
fun HomeScreen(
    // Funciones de navegación pasadas desde NavGraph
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoMolduras: () -> Unit,
    onGoCuadros: () -> Unit,
    products: List<ProductEntity>
) {
    // Destacados: toma hasta 6 productos del catálogo
    val popularProducts: List<ProductHome> = products.take(6).map { p ->
        // Si imagePath apunta a drawables que ya tenemos, asigna id; de lo contrario, null
        val resId = when (p.imagePath) {
            "moldura1" -> R.drawable.moldura1
            "moldura2" -> R.drawable.moldura2
            "moldura3" -> R.drawable.moldura3
            else -> null
        }
        ProductHome(title = p.name, price = p.price, imageResId = resId, imagePath = p.imagePath)
    }
    val services = listOf(
        ServiceHome("🖼️", "Enmarcación Personalizada", "Creamos marcos a medida para cualquier obra."),
        ServiceHome("🚚", "Despacho a Domicilio", "Entregamos tus cuadros directamente en tu hogar."),
        ServiceHome("⚡", "Servicio Express", "Enmarcación rápida en 24-48 horas.")
    )

    // Usamos LazyColumn para que toda la pantalla sea desplazable verticalmente
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // Usamos el color de fondo claro directamente (similar a --light-bg)
            .background(Color(0xFFFAF8F5))
    ) {
        // --- 1. Banner Principal y Tarjeta Superpuesta ---
        item {
            // Box permite superponer elementos
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    // =====> AQUÍ VA TU IMAGEN DEL BANNER <=====
                    painter = painterResource(id = R.drawable.legacy_frame_banner), // Reemplaza f_ban por tu archivo
                    contentDescription = "Banner Legacy Frames",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp), // Altura del banner
                    contentScale = ContentScale.Crop // Asegura que la imagen cubra el espacio
                )
                // Tarjeta superpuesta (Surface crea la tarjeta con sombra)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // Márgenes laterales
                        .align(Alignment.BottomCenter) // Alinea la tarjeta abajo del Box
                        // Offset mueve la tarjeta hacia arriba para superponerla
                        .offset(y = 60.dp),
                    shape = RoundedCornerShape(16.dp), // Bordes redondeados
                    shadowElevation = 8.dp, // Sombra
                    color = Color.White // Fondo blanco de la tarjeta
                ) {
                    // Contenido de la tarjeta
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally // Centra el texto
                    ) {
                        // Logo de marca en lugar del texto
                        com.example.legacyframeapp.ui.components.BrandLogoLarge(
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Espacio vertical pequeño
                        Text(
                            "Tradición y calidad en enmarcación desde 1998",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center, // Texto centrado
                            color = Color(0xFF6c757d) // Color texto claro (--text-light)
                        )
                    }
                }
            }
        }

        // Espacio para compensar la altura de la tarjeta superpuesta
        item { Spacer(modifier = Modifier.height(76.dp)) } // Ajusta si es necesario

        // --- 2. Accesos rápidos ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            QuickActionsRow(
                onGoMolduras = onGoMolduras,
                onGoCuadros = onGoCuadros
            )
        }

        // --- 3. Productos Populares ---
        item {
            SectionTitle("Nuestros Productos Más Populares") // Título reutilizable
            // Fila horizontal que permite scroll si hay muchos productos
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp), // Espacio en los bordes
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre tarjetas
            ) {
                // Itera sobre la lista de productos y crea una tarjeta para cada uno
                items(popularProducts) { product ->
                    ProductCardHome(product) // Llama al Composable de la tarjeta
                }
            }
        }

        // --- 4. Nuestros Servicios ---
        item {
            Spacer(modifier = Modifier.height(32.dp)) // Espacio antes de la sección
            SectionTitle("Nuestros Servicios") // Título reutilizable
            // Columna para listar los servicios verticalmente
            Column(
                modifier = Modifier.padding(horizontal = 16.dp), // Márgenes laterales
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre tarjetas
            ) {
                // Itera sobre los servicios y crea una tarjeta para cada uno
                services.forEach { service ->
                    ServiceCardHome(service) // Llama al Composable de la tarjeta
                }
            }
        }

        // (CTA inferior eliminado a solicitud del usuario)
    }
}

// --- Componentes Reutilizables (para no repetir código) ---

// Composable para Título de Sección (Recrea el estilo de tu CSS)
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge, // Tamaño de título
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Línea decorativa debajo del título
        HorizontalDivider(
            modifier = Modifier
                .width(60.dp) // Ancho de la línea
                .padding(top = 4.dp), // Separación del texto
            thickness = 3.dp, // Grosor
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

// Composable para Tarjeta de Producto Popular
@Composable
fun ProductCardHome(product: ProductHome) {
    Card(
        modifier = Modifier.width(200.dp), // Ancho fijo para las tarjetas
        elevation = CardDefaults.cardElevation(4.dp), // Sombra ligera
        shape = RoundedCornerShape(12.dp) // Bordes redondeados
    ) {
        Column {
            when {
                product.imageResId != null -> {
                    Image(
                        painter = painterResource(id = product.imageResId),
                        contentDescription = product.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                product.imagePath?.startsWith("http") == true -> {
                    AsyncImage(
                        model = product.imagePath,
                        contentDescription = product.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                product.imagePath?.let { File(it) }?.exists() == true -> {
                    AsyncImage(
                        model = File(product.imagePath!!),
                        contentDescription = product.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    // Placeholder cuando no hay imagen disponible
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFFEFEFEF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = null, tint = Color.Gray)
                    }
                }
            }
            // Contenido de texto de la tarjeta
            Column(Modifier.padding(12.dp)) {
                Text(
                    product.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1, // Limita el título a una línea
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text("$ ${product.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onGoMolduras: () -> Unit,
    onGoCuadros: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionChip(
            icon = Icons.Default.Category,
            label = "Molduras",
            onClick = onGoMolduras,
            modifier = Modifier.weight(1f)
        )
        ActionChip(
            icon = Icons.Default.Collections,
            label = "Cuadros",
            onClick = onGoCuadros,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppButton(
        onClick = onClick,
        modifier = modifier.height(48.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label)
        }
    }
}

// Composable para Tarjeta de Servicio
@Composable
fun ServiceCardHome(service: ServiceHome) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        // Color de fondo ligeramente diferente para destacar
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F0EB)) // Un color crema claro
    ) {
        // Fila para el icono y el texto
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Centra verticalmente
        ) {
            // Icono (emoji)
            Text(service.icon, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre icono y texto
            // Textos del servicio
            Column {
                Text(service.title, fontWeight = FontWeight.Bold)
                Text(service.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

