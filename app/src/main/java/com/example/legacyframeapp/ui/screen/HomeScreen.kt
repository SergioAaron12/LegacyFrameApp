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



// Data classes para simular los datos de tu web
data class ProductHome(val imageResId: Int, val title: String, val description: String)
data class ServiceHome(val icon: String, val title: String, val description: String)

@Composable
fun HomeScreen(
    // Funciones de navegación pasadas desde NavGraph
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    // Añadir más navegaciones aquí
) {
    // Datos de ejemplo simulando tu Home.tsx/html
    val popularProducts = listOf(

        ProductHome(R.drawable.moldura1, "I 09 Greca ZO", "Moldura greca clásica con diseño tradicional ZO, perfecta para fotografías familiares y documentos."),
        ProductHome(R.drawable.moldura2, "Rústica de Campo", "Acabado natural que resalta la veta de la madera, ideal para ambientes cálidos."),
        ProductHome(R.drawable.moldura3, "Nativa Alerce", "Calidez y elegancia con madera nativa de primera calidad, acabado premium.")
    )
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
                        Text(
                            "Legacy Frames",
                            style = MaterialTheme.typography.headlineMedium, // Tamaño de título
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5C2A) // Color primario (--primary-color)
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

        // --- 2. Productos Populares ---
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

        // --- 3. Nuestros Servicios ---
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

        // --- 4. Call to Action (Llamado a la acción) ---
        item {
            Spacer(modifier = Modifier.height(40.dp)) // Espacio antes de la sección
            // Caja con fondo de color primario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8B5C2A)) // Color primario (--primary-color)
                    .padding(vertical = 32.dp, horizontal = 16.dp) // Espaciado interno
            ) {
                // Contenido centrado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "¿Listo para Enmarcar tu Historia?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White, // Texto blanco
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Fila para los botones
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Botón Contactar (Fondo blanco, texto primario)
                        Button(
                            // onClick = onGoContacto, // Necesitarías añadir esta navegación
                            onClick = {}, // Acción de placeholder por ahora
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White, // Fondo blanco
                                contentColor = Color(0xFF8B5C2A) // Texto color primario
                            )
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contactar")
                        }
                        // Botón Únete (Borde blanco, texto blanco)
                        OutlinedButton(
                            onClick = onGoRegister, // Navega a Registro
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White), // Texto blanco
                            // Definición del borde
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color.White), // Borde blanco
                                width = 1.5.dp // Grosor del borde
                            )
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
            color = Color(0xFF5C3A1A) // Color oscuro (--dark-color)
        )
        // Línea decorativa debajo del título
        Divider(
            modifier = Modifier
                .width(60.dp) // Ancho de la línea
                .padding(top = 4.dp), // Separación del texto
            thickness = 3.dp, // Grosor
            color = Color(0xFFD4A574) // Color de acento (--accent-color)
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
            Image(
                // =====> AQUÍ VA TU IMAGEN DE PRODUCTO <=====
                painter = painterResource(id = product.imageResId), // Usa la ID de la imagen del producto
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp) // Altura fija para la imagen
                    // Redondea solo las esquinas superiores
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop // Cubre el espacio
            )
            // Contenido de texto de la tarjeta
            Column(Modifier.padding(12.dp)) {
                Text(
                    product.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1, // Limita el título a una línea
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3, // Limita la descripción a 3 líneas
                    color = Color.Gray // Texto gris claro
                )
            }
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

