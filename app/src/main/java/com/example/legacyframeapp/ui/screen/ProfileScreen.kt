package com.example.legacyframeapp.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import com.example.legacyframeapp.ui.components.AppButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreenVm(
    vm: AuthViewModel,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val session by vm.session.collectAsStateWithLifecycle()
    ProfileScreen(
        isLoggedIn = session.isLoggedIn,
        displayName = session.currentUser?.nombre ?: "",
        email = session.currentUser?.email ?: "",
        onGoLogin = onGoLogin,
        onGoRegister = onGoRegister,
        onGoSettings = onGoSettings,
        onLogout = onLogout
    )
}

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    displayName: String,
    email: String,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoSettings: () -> Unit,
    onLogout: () -> Unit
) {
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Cargar la imagen guardada al iniciar
    LaunchedEffect(email) {
        if (email.isNotBlank()) {
            val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            val savedUri = prefs.getString("profile_image_$email", null)
            profileImageUri = savedUri?.let { Uri.parse(it) }
        }
    }
    
    // Función para guardar la imagen
    fun saveProfileImage(uri: Uri) {
        if (email.isNotBlank()) {
            val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("profile_image_$email", uri.toString()).apply()
            profileImageUri = uri
        }
    }
    
    // Launcher para seleccionar imagen de la galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            tempImageUri = uri
            showConfirmDialog = true
        }
    }
    
    // Diálogo de confirmación
    if (showConfirmDialog && tempImageUri != null) {
        AlertDialog(
            onDismissRequest = { 
                showConfirmDialog = false
                tempImageUri = null
            },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("¿Deseas cambiar tu foto de perfil y guardarla en tu cuenta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        tempImageUri?.let { saveProfileImage(it) }
                        showConfirmDialog = false
                        tempImageUri = null
                    }
                ) {
                    Text("Sí, cambiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        tempImageUri = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Perfil", style = MaterialTheme.typography.headlineSmall)

            // Foto de perfil
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    // Mostrar imagen seleccionada
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Icono por defecto
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Botón para cambiar foto (solo si está logueado)
                if (isLoggedIn) {
                    IconButton(
                        onClick = {
                            pickImageLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isLoggedIn) {
                ListItem(
                    headlineContent = { Text(displayName.ifBlank { "Usuario" }) },
                    supportingContent = { Text(email) },
                    modifier = Modifier.fillMaxWidth()
                )
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = onGoSettings) { Text("Configuraciones") }
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = onLogout) { Text("Cerrar sesión") }
            } else {
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = onGoLogin) { Text("Iniciar sesión") }
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = onGoRegister) { Text("Registrarte") }
                ListItem(
                    headlineContent = { Text("Configuraciones") },
                    supportingContent = { Text("Compras, términos y modo oscuro") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = onGoSettings) { Text("Ir a Configuraciones") }
            }
        }
    }
}
