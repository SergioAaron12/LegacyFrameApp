package com.example.legacyframeapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: AuthViewModel,
    onLogout: () -> Unit
) {
    val state by vm.profile.collectAsState()
    val session by vm.session.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(session.isLoggedIn) {
        if (session.isLoggedIn) vm.loadUserProfile()
    }

    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                actions = {
                    if (session.isLoggedIn) {
                        IconButton(onClick = onLogout) {
                            Icon(painter = painterResource(android.R.drawable.ic_lock_power_off), contentDescription = "Salir", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (!session.isLoggedIn) {
            // NO LOGUEADO
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                Text("Inicia sesión para ver tu perfil", style = MaterialTheme.typography.titleMedium)
            }
        } else if (state.isLoading) {
            // CARGANDO
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            // LOGUEADO - DATOS
            Column(
                modifier = Modifier.padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                }

                Text("Información Personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Campos: habilitados solo si isEditing es TRUE
                val isEditable = state.isEditing

                OutlinedTextField(
                    value = state.email, onValueChange = {}, label = { Text("Email (Fijo)") },
                    enabled = false, // Email nunca se edita
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.DarkGray)
                )

                OutlinedTextField(value = state.nombre, onValueChange = { vm.onProfileChange(nombre = it) }, label = { Text("Nombre") }, enabled = isEditable, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.apellido, onValueChange = { vm.onProfileChange(apellido = it) }, label = { Text("Apellido") }, enabled = isEditable, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.telefono, onValueChange = { vm.onProfileChange(telefono = it) }, label = { Text("Teléfono") }, enabled = isEditable, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = state.direccion, onValueChange = { vm.onProfileChange(direccion = it) }, label = { Text("Dirección") }, enabled = isEditable, modifier = Modifier.fillMaxWidth(), minLines = 2)

                if (isEditable) {
                    HorizontalDivider()
                    Text("Seguridad (Opcional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = state.password, onValueChange = { vm.onProfileChange(pass = it) }, label = { Text("Nueva Contraseña") }, enabled = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = state.confirmPassword, onValueChange = { vm.onProfileChange(confirm = it) }, label = { Text("Confirmar Contraseña") }, enabled = true, modifier = Modifier.fillMaxWidth(), isError = state.password.isNotEmpty() && state.password != state.confirmPassword)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTONES DE ACCIÓN
                if (!state.isEditing) {
                    // MODO LECTURA: Botón Editar
                    Button(
                        onClick = { vm.startEditing() },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Editar Perfil")
                    }
                } else {
                    // MODO EDICIÓN: Cancelar y Guardar
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { vm.cancelEditing() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Icon(Icons.Default.Cancel, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cancelar")
                        }

                        Button(
                            onClick = { vm.saveProfile() },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Default.Save, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar")
                        }
                    }
                }

                if (state.errorMsg != null) {
                    Text(state.errorMsg ?: "", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}