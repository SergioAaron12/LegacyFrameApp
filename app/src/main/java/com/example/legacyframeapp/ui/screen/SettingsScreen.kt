package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.legacyframeapp.ui.components.AppButton
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun SettingsScreenVm(
    vm: AuthViewModel,
    onGoPurchases: () -> Unit,
    onGoTerms: () -> Unit,
    onGoContact: () -> Unit,
    onBack: () -> Unit = {},
    onGoLogin: () -> Unit = {}
) {
    val darkMode by vm.darkMode.collectAsStateWithLifecycle()
    val session by vm.session.collectAsStateWithLifecycle()
    val themeMode by vm.themeMode.collectAsStateWithLifecycle()
    val accentHex by vm.accentColor.collectAsStateWithLifecycle()
    val fontScale by vm.fontScale.collectAsStateWithLifecycle()
    val notifOffers by vm.notifOffers.collectAsStateWithLifecycle()
    val notifTracking by vm.notifTracking.collectAsStateWithLifecycle()
    val notifCart by vm.notifCart.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    SettingsScreen(
        isLoggedIn = session.isLoggedIn,
        themeMode = themeMode,
        onThemeModeChange = { m -> scope.launch { vm.setThemeMode(m) } },
        accentHex = accentHex,
        onAccentChange = { hex -> scope.launch { vm.setAccentColor(hex) } },
        fontScale = fontScale,
        onFontScaleChange = { fs -> scope.launch { vm.setFontScale(fs) } },
        notifOffers = notifOffers,
        onNotifOffersChange = { b -> scope.launch { vm.setNotifOffers(b) } },
        notifTracking = notifTracking,
        onNotifTrackingChange = { b -> scope.launch { vm.setNotifTracking(b) } },
        notifCart = notifCart,
        onNotifCartChange = { b -> scope.launch { vm.setNotifCart(b) } },
        onGoPurchases = onGoPurchases,
        onGoTerms = onGoTerms,
        onGoContact = onGoContact,
        onGoLogin = onGoLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isLoggedIn: Boolean,
    themeMode: String,
    onThemeModeChange: (String) -> Unit,
    accentHex: String,
    onAccentChange: (String) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit,
    notifOffers: Boolean,
    onNotifOffersChange: (Boolean) -> Unit,
    notifTracking: Boolean,
    onNotifTrackingChange: (Boolean) -> Unit,
    notifCart: Boolean,
    onNotifCartChange: (Boolean) -> Unit,
    onGoPurchases: () -> Unit,
    onGoTerms: () -> Unit,
    onGoContact: () -> Unit,
    onGoLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuración", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección: Apariencia
            SettingsSection(title = "Apariencia") {
                // Modo de tema
                SettingItem(
                    title = "Tema",
                    icon = Icons.Default.Star
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "light" to "Claro",
                            "dark" to "Oscuro",
                            "system" to "Sistema"
                        ).forEach { (value, label) ->
                            FilterChip(
                                selected = themeMode == value,
                                onClick = { onThemeModeChange(value) },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Color de acento
                SettingItem(
                    title = "Color de Acento",
                    icon = Icons.Default.Star
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val accents = listOf(
                            "#FF8B5C2A" to "Marrón",
                            "#FF6F451A" to "Madera",
                            "#FF2E7D32" to "Verde",
                            "#FFB0793E" to "Dorado"
                        )
                        accents.forEach { (hex, name) ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (e: Exception) {
                                Color.Gray
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(color)
                                        .clickable { onAccentChange(hex) }
                                        .border(
                                            width = if (accentHex == hex) 3.dp else 1.dp,
                                            color = if (accentHex == hex) 
                                                MaterialTheme.colorScheme.primary 
                                            else 
                                                MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (accentHex == hex) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Seleccionado",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    name,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Tamaño de fuente
                SettingItem(
                    title = "Tamaño de Texto",
                    subtitle = String.format("%.0f%%", fontScale * 100),
                    icon = Icons.Default.Star
                ) {
                    Slider(
                        value = fontScale,
                        onValueChange = onFontScaleChange,
                        valueRange = 0.85f..1.30f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Sección: Notificaciones
            SettingsSection(title = "Notificaciones") {
                NotificationToggle(
                    label = "Ofertas y Promociones",
                    subtitle = "Recibe notificaciones sobre descuentos especiales",
                    checked = notifOffers,
                    onChange = onNotifOffersChange
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NotificationToggle(
                    label = "Seguimiento de Pedidos",
                    subtitle = "Actualizaciones del estado de tu compra",
                    checked = notifTracking,
                    onChange = onNotifTrackingChange
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NotificationToggle(
                    label = "Recordatorios de Carrito",
                    subtitle = "Te recordaremos los productos guardados",
                    checked = notifCart,
                    onChange = onNotifCartChange
                )
            }

            // Sección: Cuenta
            SettingsSection(title = "Cuenta") {
                if (isLoggedIn) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Gestiona tu información personal, direcciones de envío y métodos de pago",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onGoPurchases
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Mis Compras")
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Inicia sesión para acceder a todas las funcionalidades",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onGoLogin
                        ) {
                            Text("Iniciar Sesión")
                        }
                    }
                }
            }

            // Sección: Soporte y Legal
            SettingsSection(title = "Soporte y Legal") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onGoContact,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Contacto")
                    }
                    OutlinedButton(
                        onClick = onGoTerms,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Términos")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Política de Privacidad • FAQ • Acerca de",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Versión 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Espaciado final
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Componentes auxiliares para mejor organización
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        content()
    }
}

@Composable
private fun NotificationToggle(
    label: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}
