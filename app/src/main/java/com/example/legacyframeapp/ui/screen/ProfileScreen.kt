package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

            if (isLoggedIn) {
                ListItem(
                    headlineContent = { Text(displayName.ifBlank { "Usuario" }) },
                    supportingContent = { Text(email) },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(modifier = Modifier.fillMaxWidth(), onClick = onGoSettings) { Text("Configuraciones") }
                Button(modifier = Modifier.fillMaxWidth(), onClick = onLogout) { Text("Cerrar sesión") }
            } else {
                Button(modifier = Modifier.fillMaxWidth(), onClick = onGoLogin) { Text("Iniciar sesión") }
                Button(modifier = Modifier.fillMaxWidth(), onClick = onGoRegister) { Text("Registrarte") }
                ListItem(
                    headlineContent = { Text("Configuraciones") },
                    supportingContent = { Text("Compras, términos y modo oscuro") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                Button(modifier = Modifier.fillMaxWidth(), onClick = onGoSettings) { Text("Ir a Configuraciones") }
            }
        }
    }
}
