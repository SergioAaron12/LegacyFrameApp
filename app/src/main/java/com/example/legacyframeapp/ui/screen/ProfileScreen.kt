package com.example.legacyframeapp.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import com.example.legacyframeapp.ui.components.AppButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.material3.OutlinedTextField
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import java.io.File

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
        lastName = session.currentUser?.apellido,
        onGoLogin = onGoLogin,
        onGoRegister = onGoRegister,
        onGoSettings = onGoSettings,
        onLogout = onLogout,
        onChangeName = { nombre, apellido -> vm.changeDisplayName(nombre, apellido) },
        onChangePassword = { current, new, onResult -> vm.changePassword(current, new, onResult) }
    )
}

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    displayName: String,
    email: String,
    lastName: String?,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoSettings: () -> Unit,
    onLogout: () -> Unit,
    onChangeName: (String, String?) -> Unit,
    onChangePassword: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // URI para la foto tomada con la cámara
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    
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
    
    // Crear URI para la cámara en almacenamiento interno persistente
    fun createImageUri(): Uri {
        val dir = File(context.filesDir, "profile_images")
        if (!dir.exists()) dir.mkdirs()
        val imageFile = File(dir, "profile_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
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
    
    // Launcher para tomar foto con la cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            tempImageUri = cameraImageUri
            showConfirmDialog = true
        }
    }

    // Permiso de cámara en tiempo de ejecución
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraImageUri = createImageUri()
            takePictureLauncher.launch(cameraImageUri!!)
        } else {
            // Fallback a galería si no concede permiso
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
    
    // Diálogo de selección de fuente (Cámara o Galería)
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar foto") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showImageSourceDialog = false
                            // Solicitar permiso de cámara y continuar
                            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tomar foto")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            showImageSourceDialog = false
                            pickImageLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Seleccionar de galería")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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
                            showImageSourceDialog = true
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
                    headlineContent = { Text((listOfNotNull(displayName.ifBlank { null }, lastName).joinToString(" ")).ifBlank { "Usuario" }) },
                    supportingContent = { Text(email) },
                    modifier = Modifier.fillMaxWidth()
                )
                var showNameDialog by remember { mutableStateOf(false) }
                var showPasswordDialog by remember { mutableStateOf(false) }
                var infoMessage by remember { mutableStateOf<String?>(null) }
                if (infoMessage != null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { infoMessage = null },
                        title = { Text("Aviso") },
                        text = { Text(infoMessage!!) },
                        confirmButton = { TextButton(onClick = { infoMessage = null }) { Text("OK") } }
                    )
                }
                if (showNameDialog) {
                    ChangeNameDialog(
                        currentFirst = displayName,
                        currentLast = lastName,
                        onDismiss = { showNameDialog = false },
                        onConfirm = { n, a -> showNameDialog = false; onChangeName(n, a); infoMessage = "Nombre actualizado" }
                    )
                }
                if (showPasswordDialog) {
                    ChangePasswordDialog(
                        onDismiss = { showPasswordDialog = false },
                        onConfirm = { current, new ->
                            onChangePassword(current, new) { ok, err ->
                                showPasswordDialog = false
                                infoMessage = if (ok) "Contraseña actualizada" else (err ?: "No se pudo actualizar")
                            }
                        }
                    )
                }
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = { showNameDialog = true }) { Text("Cambiar nombre") }
                AppButton(modifier = Modifier.fillMaxWidth(), onClick = { showPasswordDialog = true }) { Text("Cambiar contraseña") }
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

@Composable
private fun ChangeNameDialog(
    currentFirst: String,
    currentLast: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var first by remember { mutableStateOf(currentFirst) }
    var last by remember { mutableStateOf(currentLast ?: "") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar nombre") },
        text = {
            Column {
                OutlinedTextField(value = first, onValueChange = { first = it }, label = { Text("Nombre") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = last, onValueChange = { last = it }, label = { Text("Apellido (opcional)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(first, last.ifBlank { null }) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar contraseña") },
        text = {
            Column {
                OutlinedTextField(value = current, onValueChange = { current = it }, label = { Text("Contraseña actual") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("Nueva contraseña") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirmar contraseña") })
                if (error != null) { Spacer(Modifier.height(8.dp)); Text(error!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (newPass != confirm) { error = "Las contraseñas no coinciden"; return@TextButton }
                if (newPass.isBlank()) { error = "La nueva contraseña es obligatoria"; return@TextButton }
                onConfirm(current, newPass)
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
