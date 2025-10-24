package com.example.legacyframeapp.ui.screen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp

@Composable
fun ContactScreen() {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Contacto")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Mensaje") },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // WhatsApp
            Button(onClick = {
                val text = "Hola, soy $name. ${if (message.isNotBlank()) "Mensaje: $message" else ""}"
                val url = "https://api.whatsapp.com/send?phone=56227916878&text=" + Uri.encode(text)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Chat, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("WhatsApp")
            }

            // Llamar
            Button(onClick = {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:56227916878")
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Llamar")
            }
        }

        // Email
        TextButton(onClick = {
            val subject = Uri.encode("Consulta desde la app")
            val body = Uri.encode("Nombre: $name\nEmail: $email\n\n$message")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app")
                putExtra(Intent.EXTRA_TEXT, "Nombre: $name\nEmail: $email\n\n$message")
            }
            context.startActivity(intent)
        }) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Enviar Email")
        }
    }
}
