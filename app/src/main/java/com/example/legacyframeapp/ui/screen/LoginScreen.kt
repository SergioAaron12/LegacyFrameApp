package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// ¡QUITAR LA PALABRA 'private' SI ESTABA AQUÍ!
@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {
    val state by vm.login.collectAsStateWithLifecycle()

    // Si el login es exitoso, navegamos
    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearLoginResult()
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.onLoginEmailChange(it) },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.errorMsg != null
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.pass,
            onValueChange = { vm.onLoginPassChange(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.errorMsg != null
        )

        if (state.errorMsg != null) {
            Text(state.errorMsg!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { vm.submitLogin() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state.canSubmit && !state.isSubmitting
        ) {
            if (state.isSubmitting) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            else Text("Ingresar")
        }

        TextButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}