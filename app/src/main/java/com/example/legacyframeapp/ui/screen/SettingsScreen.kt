package com.example.legacyframeapp.ui.screen

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: AuthViewModel,
    onBack: () -> Unit
) {
    // --- ESTADOS DESDE EL VIEWMODEL ---
    val themeMode by vm.themeMode.collectAsStateWithLifecycle()
    val accentColor by vm.accentColor.collectAsStateWithLifecycle()
    val fontScale by vm.fontScale.collectAsStateWithLifecycle()

    val notifOffers by vm.notifOffers.collectAsStateWithLifecycle()
    val notifTracking by vm.notifTracking.collectAsStateWithLifecycle()
    val notifCart by vm.notifCart.collectAsStateWithLifecycle()

    val session by vm.session.collectAsStateWithLifecycle()

    // Estado local para el diálogo de contacto
    var showContactDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. APARIENCIA
            SettingsSection(title = "Apariencia", icon = Icons.Default.BrightnessMedium) {
                Text("Tema de la aplicación", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ThemeOption(label = "Claro", selected = themeMode == "light") {
                        scope.launch { vm.setThemeMode("light") }
                    }
                    ThemeOption(label = "Oscuro", selected = themeMode == "dark") {
                        scope.launch { vm.setThemeMode("dark") }
                    }
                    ThemeOption(label = "Sistema", selected = themeMode == "system") {
                        scope.launch { vm.setThemeMode("system") }
                    }
                }
            }

            HorizontalDivider()

            // 2. ACENTO DE COLOR
            SettingsSection(title = "Color de Acento", icon = Icons.Default.ColorLens) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val colors = listOf(
                        "#FF8B5C2A" to "Naranja", // Tu color original
                        "#FF2E7D32" to "Verde",
                        "#FF1976D2" to "Azul",
                        "#FFD32F2F" to "Rojo"
                    )

                    colors.forEach { (hex, name) ->
                        ColorCircle(
                            colorHex = hex,
                            isSelected = accentColor == hex,
                            onClick = { scope.launch { vm.setAccentColor(hex) } }
                        )
                    }
                }
            }

            HorizontalDivider()

            // 3. TAMAÑO DE FUENTE
            SettingsSection(title = "Tamaño de Fuente", icon = Icons.Default.TextFields) {
                Text("Escala: ${(fontScale * 100).toInt()}%")
                Slider(
                    value = fontScale,
                    onValueChange = { scope.launch { vm.setFontScale(it) } },
                    valueRange = 0.8f..1.3f,
                    steps = 4
                )
            }

            HorizontalDivider()

            // 4. NOTIFICACIONES
            SettingsSection(title = "Notificaciones", icon = Icons.Default.Notifications) {
                SwitchRow(label = "Ofertas y Promociones", checked = notifOffers) {
                    scope.launch { vm.setNotifOffers(it) }
                }
                SwitchRow(label = "Seguimiento de Pedidos", checked = notifTracking) {
                    scope.launch { vm.setNotifTracking(it) }
                }
                SwitchRow(label = "Carrito Abandonado", checked = notifCart) {
                    scope.launch { vm.setNotifCart(it) }
                }
            }

            HorizontalDivider()

            // 5. CONTACTO
            Button(
                onClick = { showContactDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Email, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Contactar Soporte")
            }
        }
    }

    // Diálogo de Contacto
    if (showContactDialog) {
        ContactDialog(
            vm = vm,
            onDismiss = { showContactDialog = false },
            isLoggedIn = session.isLoggedIn,
            userEmail = session.currentUser?.email ?: ""
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun SettingsSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

@Composable
fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) { { Icon(Icons.Default.BrightnessMedium, null) } } else null
    )
}

@Composable
fun ColorCircle(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = Color(android.graphics.Color.parseColor(colorHex))
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ContactDialog(vm: AuthViewModel, onDismiss: () -> Unit, isLoggedIn: Boolean, userEmail: String) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(if (isLoggedIn) userEmail else "") }
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contáctanos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Mensaje") }, minLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSending = true
                    vm.sendContactMessage(name, email, message) { success ->
                        isSending = false
                        if (success) {
                            Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Error al enviar", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isSending && name.isNotBlank() && email.isNotBlank() && message.isNotBlank()
            ) {
                if (isSending) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}