package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import com.example.legacyframeapp.ui.components.AppButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import com.example.legacyframeapp.R
import kotlinx.coroutines.launch

@Composable
fun ProfileScreenVm(
    vm: AuthViewModel,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val session by vm.session.collectAsStateWithLifecycle()
    val avatarType by vm.avatarType.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    ProfileScreen(
        isLoggedIn = session.isLoggedIn,
        displayName = session.currentUser?.nombre ?: "",
        email = session.currentUser?.email ?: "",
        avatarType = avatarType,
        onSelectAvatar = { type -> scope.launch { vm.setAvatarType(type) } },
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
    avatarType: String,
    onSelectAvatar: (String) -> Unit,
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

            // Avatar actual
            val avatarRes = if (avatarType == "female") R.drawable.ic_avatar_female else R.drawable.ic_avatar_male
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Avatar",
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = if (avatarType == "female") "Avatar: Mujer" else "Avatar: Hombre",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppButton(onClick = { onSelectAvatar("male") }) { Text("Hombre") }
                AppButton(onClick = { onSelectAvatar("female") }) { Text("Mujer") }
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
