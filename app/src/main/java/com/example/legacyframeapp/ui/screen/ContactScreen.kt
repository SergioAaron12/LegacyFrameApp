package com.example.legacyframeapp.ui.screen

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.legacyframeapp.ui.components.AppButton
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// -----------------------------------------------------------------
// 1. Composable "Stateful" (Conectado al ViewModel)
// -----------------------------------------------------------------
@Composable
fun ContactScreenVm(
    vm: AuthViewModel
) {
    val context = LocalContext.current
    // Eliminamos 'scope' que no se usaba
    var isSending by remember { mutableStateOf(false) }

    ContactScreen(
        isSending = isSending,
        onSendMessage = { name, email, msg, onSuccess ->
            isSending = true
            // Llamada a la API a través del ViewModel
            vm.sendContactMessage(name, email, msg) { success ->
                isSending = false
                if (success) {
                    // Éxito: Notificación y Toast
                    showSuccessNotification(context)
                    Toast.makeText(context, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show()
                    onSuccess() // Limpiar el formulario
                } else {
                    // Error
                    Toast.makeText(context, "Error al enviar. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

// -----------------------------------------------------------------
// 2. Composable "Stateless" (Solo UI)
// -----------------------------------------------------------------
@Composable
fun ContactScreen(
    isSending: Boolean,
    onSendMessage: (String, String, String, () -> Unit) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var messageError by remember { mutableStateOf<String?>(null) }

    // --- Funciones de Validación Local ---
    fun validateMessage(text: String): String? {
        val len = text.trim().length
        if (len < 10) return "El mensaje debe tener al menos 10 caracteres"
        if (len > 300) return "El mensaje no puede superar 300 caracteres"
        return null
    }

    fun validateEmailLocal(text: String): String? {
        if (text.isBlank()) return "El correo es obligatorio"
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return if (pattern.matcher(text).matches()) null else "Correo inválido"
    }

    fun validateNameLocal(text: String): String? {
        if (text.isBlank()) return "El nombre es obligatorio"
        return null
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Contáctanos", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Envíanos tus dudas sobre enmarcaciones o pedidos especiales.", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(8.dp))

        // Campo Nombre
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = validateNameLocal(it)
            },
            label = { Text("Nombre") },
            isError = nameError != null,
            supportingText = {
                if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = validateEmailLocal(it)
            },
            label = { Text("Correo") },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Mensaje
        OutlinedTextField(
            value = message,
            onValueChange = {
                val limited = it.take(300)
                message = limited
                messageError = validateMessage(limited)
            },
            label = { Text("Mensaje") },
            isError = messageError != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            supportingText = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (messageError != null) messageError!! else "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text("${message.length}/300", style = MaterialTheme.typography.labelSmall)
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        // Botones de acción rápida (WhatsApp / Llamar)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // WhatsApp
            Button(
                onClick = {
                    Toast.makeText(context, "Abriendo WhatsApp...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("WhatsApp")
            }

            // Llamar
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL)
                    // Corrección KTX: toUri()
                    intent.data = "tel:56945621740".toUri()
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f)
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Llamar")
            }
        }

        // Botón Enviar Mensaje (Conecta con API)
        AppButton(
            onClick = {
                val msgErr = validateMessage(message)
                val nErr = validateNameLocal(name)
                val eErr = validateEmailLocal(email)
                nameError = nErr
                emailError = eErr
                messageError = msgErr

                if (msgErr == null && nErr == null && eErr == null) {
                    onSendMessage(name, email, message) {
                        // Callback de limpieza si fue exitoso
                        name = ""
                        email = ""
                        message = ""
                        nameError = null
                        emailError = null
                        messageError = null
                    }
                } else {
                    Toast.makeText(context, "Por favor corrige los errores", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isSending,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 8.dp)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp), // Esto ya no dará error
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Enviando...")
            } else {
                Text("Enviar mensaje")
            }
        }
    }
}

// Función auxiliar para mostrar notificación local
private fun showSuccessNotification(context: Context) {
    val channelId = "contact_channel"
    val nm = context.getSystemService(NotificationManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Contacto", NotificationManager.IMPORTANCE_DEFAULT)
        nm?.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_notify_chat)
        .setContentTitle("Mensaje recibido")
        .setContentText("Hemos recibido tu mensaje. Te contactaremos pronto.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    val canNotify = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else true

    if (canNotify) {
        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }
}