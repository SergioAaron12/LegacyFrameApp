package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onGoLogin: () -> Unit
) {
    // Observamos el estado del registro desde el ViewModel
    val state by vm.register.collectAsStateWithLifecycle()

    // Efecto: Si el registro es exitoso, navegamos al Login
    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearRegisterResult()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Scroll por si la pantalla es chica
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(24.dp))

        // --- CAMPOS DE FORMULARIO ---

        OutlinedTextField(
            value = state.nombre,
            onValueChange = { vm.onRegisterNombreChange(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.nombreError != null,
            supportingText = { state.nombreError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = state.apellido,
            onValueChange = { vm.onRegisterApellidoChange(it) },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.apellidoError != null,
            supportingText = { state.apellidoError?.let { Text(it) } }
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.rut,
                onValueChange = { vm.onRegisterRutChange(it) },
                label = { Text("RUT (Sin puntos)") },
                modifier = Modifier.weight(0.7f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.rutError != null,
                supportingText = { state.rutError?.let { Text(it) } }
            )
            OutlinedTextField(
                value = state.dv,
                onValueChange = { vm.onRegisterDvChange(it) },
                label = { Text("DV") },
                modifier = Modifier.weight(0.3f),
                isError = state.dvError != null
            )
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.onRegisterEmailChange(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = state.emailError != null,
            supportingText = { state.emailError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = state.phone,
            onValueChange = { vm.onRegisterPhoneChange(it) },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = state.phoneError != null,
            supportingText = { state.phoneError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = state.pass,
            onValueChange = { vm.onRegisterPassChange(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.passError != null,
            supportingText = { state.passError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = state.confirm,
            onValueChange = { vm.onRegisterConfirmChange(it) },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.confirmError != null,
            supportingText = { state.confirmError?.let { Text(it) } }
        )

        if (state.errorMsg != null) {
            Text(
                text = state.errorMsg!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // --- BOTONES ---

        Button(
            onClick = { vm.submitRegister() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state.canSubmit && !state.isSubmitting
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Registrarse")
            }
        }

        TextButton(
            onClick = onGoLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}