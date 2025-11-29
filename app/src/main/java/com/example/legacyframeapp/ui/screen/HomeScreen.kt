package com.example.legacyframeapp.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.legacyframeapp.R
import com.example.legacyframeapp.data.local.product.ProductEntity
import com.example.legacyframeapp.ui.components.AppButton
import coil.compose.AsyncImage



// Modelos de UI para la pantalla Home
data class ProductHome(
    val title: String, 
    val price: Int, 
    val imageResId: Int? = null, 
    val imagePath: String? = null,
    val description: String = ""
)
data class ServiceHome(val icon: String, val title: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Funciones de navegación pasadas desde NavGraph
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoMolduras: () -> Unit,
    onGoCuadros: () -> Unit,
    products: List<ProductEntity>
) {
    // Destacados: toma 3 productos del catálogo (los más destacados)
    val popularProducts: List<ProductHome> = products.take(3).map { p ->
        // Mapeo de imágenes locales
        val resId = when (p.imagePath) {
            "moldura1" -> R.drawable.moldura1
            "moldura2" -> R.drawable.moldura2
            "moldura3" -> R.drawable.moldura3
            "p15_greca_plata" -> R.drawable.p15_greca_plata
            "h20_albayalde_azul" -> R.drawable.h20_albayalde_azul
            "b10_alerce" -> R.drawable.b10_alerce
            "j16_nativa" -> R.drawable.j16_nativa
            "p12_finger_joint" -> R.drawable.p12_finger_joint
            else -> null
        }
        ProductHome(
            title = p.name, 
            price = p.price, 
            imageResId = resId, 
            imagePath = p.imagePath,
            description = p.description
        )
    }
    val services = listOf(
        ServiceHome("🖼️", "Enmarcación Personalizada", "Creamos marcos a medida para cualquier obra."),
        ServiceHome("🚚", "Despacho a Domicilio", "Entregamos tus cuadros directamente en tu hogar."),
        ServiceHome("⚡", "Servicio Express", "Enmarcación rápida en 24-48 horas.")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.height(32.dp)
            )
        }
    ) { inner ->
        // Contenido principal scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(inner)
        ) {
            // Sección 1: Banner principal
            item {
                // Imagen del header con el logo y las fotos
                Image(
                    painter = painterResource(id = R.drawable.home_header),
                    contentDescription = "Header",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }
        
            // Sección 2: Tarjeta de bienvenida y navegación a Molduras/Cuadros
            item {
                // Tarjeta con logo, texto y botones de navegación
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp), // Bordes redondeados
                    shadowElevation = 8.dp, // Sombra
                    color = MaterialTheme.colorScheme.surface // Fondo crema del tema
                ) {
                    // Contenido de la tarjeta
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo de marca
                        com.example.legacyframeapp.ui.components.BrandLogoLarge(
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tradición y calidad en enmarcación desde 1998",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Botones de acceso rápido a Molduras y Cuadros
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            AppButton(
                                onClick = onGoMolduras,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colorsOverride = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = Color.White,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Molduras")
                                }
                            }
                            AppButton(
                                onClick = onGoCuadros,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colorsOverride = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = Color.White,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Cuadros")
                                }
                            }
                        }
                    }
                }
            }
            // Sección 3: Productos más vendidos (los 3 primeros del catálogo)
            item {
                SectionTitle("Nuestros Más Vendidos") // Título reutilizable
                // Fila horizontal que permite scroll si hay muchos productos
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp), // Espacio en los bordes
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre tarjetas
                ) {
                    // Itera sobre la lista de productos y crea una tarjeta para cada uno
                    items(popularProducts) { product ->
                        ProductCardHome(
                            product = product,
                            onClick = onGoMolduras // Al hacer clic va a Molduras
                        )
                    }
                }
            }
            // Sección 4: Servicios ofrecidos
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
}

// Componentes reutilizables

// Título de sección con línea decorativa
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

// Tarjeta de producto popular (imagen, título, precio y descripción corta)
@Composable
fun ProductCardHome(product: ProductHome, onClick: () -> Unit) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() }, // Añade clickable
        elevation = CardDefaults.cardElevation(4.dp), // Sombra ligera
        shape = RoundedCornerShape(12.dp) // Bordes redondeados
    ) {
        Column {
            // Imagen del producto: desde recurso o ruta/URL
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
                !product.imagePath.isNullOrEmpty() -> {
                    // Intenta cargar desde drawable primero
                    val drawableId = context.resources.getIdentifier(
                        product.imagePath,
                        "drawable",
                        context.packageName
                    )
                    
                    if (drawableId != 0) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Si no está en drawable, intenta cargar desde archivo o URL
                        AsyncImage(
                            model = product.imagePath,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_foreground)
                        )
                    }
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
                // Descripción breve
                if (product.description.isNotEmpty()) {
                    Text(
                        product.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6c757d),
                        maxLines = 2,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    "$${String.format("%,d", product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(
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
fun ActionChip(
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Crema del tema
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
