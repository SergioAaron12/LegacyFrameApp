package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun ContactScreen(vm: AuthViewModel) {
    val session by vm.session.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Contacto", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (sent) {
            Text("¡Gracias! Tu mensaje ha sido enviado.", color = MaterialTheme.colorScheme.primary)
            Button(onClick = { sent = false; message = "" }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Enviar otro")
            }
        } else {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Escribe tu consulta aquí") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.sendContactMessage(
                        session.currentUser?.nombre ?: "Invitado",
                        session.currentUser?.email ?: "anonimo@legacy.cl",
                        message
                    ) { if(it) sent = true }
                },
                enabled = message.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Mensaje")
            }
        }
    }
}