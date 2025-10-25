package com.example.legacyframeapp.ui.screen

import android.content.Intent
import android.net.Uri
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.legacyframeapp.ui.components.AppButton

@Composable
fun ContactScreen() {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }

    fun validateMessage(text: String): String? {
        val len = text.trim().length
        if (len < 10) return "El mensaje debe tener al menos 10 caracteres"
        if (len > 300) return "El mensaje no puede superar 300 caracteres"
        return null
    }

    fun postSuccessNotification() {
        val channelId = "contact_channel"
        val nm = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Contacto", NotificationManager.IMPORTANCE_DEFAULT)
            nm?.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Mensaje enviado")
            .setContentText("Mensaje enviado con éxito")
            .setAutoCancel(true)
        val canNotify = if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
        if (canNotify) {
            NotificationManagerCompat.from(context).notify(1001, builder.build())
        }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Contacto")

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = validateNameLocal(it)
            },
            label = { Text("Nombre") },
            isError = nameError != null,
            supportingText = {
                if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = validateEmailLocal(it)
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = message,
            onValueChange = {
                // Limitar a 300 caracteres y validar
                val limited = it.take(300)
                message = limited
                messageError = validateMessage(limited)
            },
            label = { Text("Mensaje") },
            isError = messageError != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            supportingText = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (messageError != null) "Debe tener mínimo 10 letras y máximo 300." else "",
                        color = if (messageError != null) MaterialTheme.colorScheme.error else Color.Unspecified,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text("${message.length}/300", style = MaterialTheme.typography.labelSmall)
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // WhatsApp (estilo del sitio)
            Button(onClick = {
                val msgErr = validateMessage(message)
                val nErr = validateNameLocal(name)
                val eErr = validateEmailLocal(email)
                nameError = nErr
                emailError = eErr
                if (msgErr != null || nErr != null || eErr != null) {
                    android.widget.Toast.makeText(context, msgErr ?: nErr ?: eErr, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    postSuccessNotification()
                    android.widget.Toast.makeText(context, "Mensaje enviado con éxito", android.widget.Toast.LENGTH_SHORT).show()
                    // Reiniciar formulario
                    name = ""
                    email = ""
                    message = ""
                    nameError = null
                    emailError = null
                    messageError = null
                }
            }, modifier = Modifier
                .height(40.dp)
                .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF25D366),
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("WhatsApp")
            }

            // Llamar (más compacto, consistente con otros botones)
            OutlinedButton(onClick = {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:56227916878")
                context.startActivity(intent)
            }, modifier = Modifier
                .height(40.dp)
                .weight(1f)
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Llamar")
            }
        }

        // Botón central de enviar mensaje (mismo flujo que WhatsApp, valida nombre/email/mensaje)
        AppButton(
            onClick = {
                val msgErr = validateMessage(message)
                val nErr = validateNameLocal(name)
                val eErr = validateEmailLocal(email)
                nameError = nErr
                emailError = eErr
                if (msgErr != null || nErr != null || eErr != null) {
                    android.widget.Toast.makeText(context, msgErr ?: nErr ?: eErr, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    postSuccessNotification()
                    android.widget.Toast.makeText(context, "Mensaje enviado con éxito", android.widget.Toast.LENGTH_SHORT).show()
                    // Reiniciar formulario
                    name = ""
                    email = ""
                    message = ""
                    nameError = null
                    emailError = null
                    messageError = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 8.dp)
        ) {
            Text("Enviar mensaje")
        }
    }
}
