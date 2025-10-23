package com.example.legacyframeapp.ui.screen // Asegúrate que el paquete sea correcto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// --- 1. Stateful Composable (Conecta con ViewModel) ---
@Composable
fun RegisterScreenVm(
    vm: AuthViewModel, // Recibe el ViewModel
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {
    // Observa el estado del ViewModel
    val state by vm.register.collectAsStateWithLifecycle()

    // Maneja la navegación cuando el registro es exitoso
    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearRegisterResult() // Limpia flag en VM
            onRegisteredNavigateLogin() // Navega
        }
    }

    // Llama al composable presentacional, pasándole el estado y los handlers del VM
    RegisterScreen(
        nombre = state.nombre,
        apellido = state.apellido,
        rut = state.rut,
        dv = state.dv,
        email = state.email,
        phone = state.phone,
        pass = state.pass,
        confirm = state.confirm,

        nombreError = state.nombreError,
        apellidoError = state.apellidoError,
        rutError = state.rutError,
        dvError = state.dvError,
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,

        onNombreChange = vm::onRegisterNombreChange, // Pasa las funciones del VM
        onApellidoChange = vm::onRegisterApellidoChange,
        onRutChange = vm::onRegisterRutChange,
        onDvChange = vm::onRegisterDvChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onRegisterPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onRegisterConfirmChange,

        onSubmit = vm::submitRegister, // Pasa la función de envío
        onGoLogin = onGoLogin          // Pasa la función de navegación a Login
    )
}


// --- 2. Stateless/Presentational Composable (Solo UI) ---
@Composable
private fun RegisterScreen(
    // Parámetros para los valores de los campos
    nombre: String,
    apellido: String,
    rut: String,
    dv: String,
    email: String,
    phone: String,
    pass: String,
    confirm: String,
    // Parámetros para los errores
    nombreError: String?,
    apellidoError: String?,
    rutError: String?,
    dvError: String?,
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    // Parámetros de estado del formulario
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    // Parámetros para los Handlers (funciones a llamar)
    onNombreChange: (String) -> Unit,
    onApellidoChange: (String) -> Unit,
    onRutChange: (String) -> Unit,
    onDvChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    // Estados locales solo para la UI (visibilidad de contraseña)
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // UI (similar a antes, pero usa los parámetros en lugar de 'state.')
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Cuenta", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // --- CAMPO NOMBRE ---
            OutlinedTextField(
                value = nombre, // Usa el parámetro 'nombre'
                onValueChange = onNombreChange, // Llama al handler 'onNombreChange'
                label = { Text("Nombre") },
                isError = nombreError != null, // Usa el parámetro 'nombreError'
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError != null) { // Usa 'nombreError'
                Text(nombreError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))

            // --- CAMPO APELLIDO ---
            OutlinedTextField(
                value = apellido, // Usa parámetro
                onValueChange = onApellidoChange, // Llama handler
                label = { Text("Apellido (Opcional)") },
                isError = apellidoError != null, // Usa parámetro
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (apellidoError != null) { // Usa parámetro
                Text(apellidoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))

            // --- CAMPOS RUT y DV ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Campo RUT
                Column(modifier = Modifier.weight(0.75f)) {
                    OutlinedTextField(
                        value = rut, // Usa parámetro
                        onValueChange = onRutChange, // Llama handler
                        label = { Text("RUT (sin DV)") },
                        isError = rutError != null, // Usa parámetro
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (rutError != null) { // Usa parámetro
                        Text(rutError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
                // Campo DV
                Column(modifier = Modifier.weight(0.25f)) {
                    OutlinedTextField(
                        value = dv, // Usa parámetro
                        onValueChange = onDvChange, // Llama handler
                        label = { Text("DV") },
                        isError = dvError != null, // Usa parámetro
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (dvError != null) { // Usa parámetro
                        Text(dvError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // --- CAMPO EMAIL ---
            OutlinedTextField(
                value = email, // Usa parámetro
                onValueChange = onEmailChange, // Llama handler
                label = { Text("Correo Electrónico") },
                isError = emailError != null, // Usa parámetro
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) { // Usa parámetro
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))

            // --- CAMPO TELÉFONO ---
            OutlinedTextField(
                value = phone, // Usa parámetro
                onValueChange = onPhoneChange, // Llama handler
                label = { Text("Teléfono") },
                isError = phoneError != null, // Usa parámetro
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError != null) { // Usa parámetro
                Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))

            // --- CAMPO CONTRASEÑA ---
            OutlinedTextField(
                value = pass, // Usa parámetro
                onValueChange = onPassChange, // Llama handler
                label = { Text("Contraseña") },
                isError = passError != null, // Usa parámetro
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "show password")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) { // Usa parámetro
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))

            // --- CAMPO CONFIRMAR CONTRASEÑA ---
            OutlinedTextField(
                value = confirm, // Usa parámetro
                onValueChange = onConfirmChange, // Llama handler
                label = { Text("Confirmar Contraseña") },
                isError = confirmError != null, // Usa parámetro
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "show confirm password")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError != null) { // Usa parámetro
                Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(16.dp))

            // --- BOTÓN REGISTRAR ---
            Button(
                onClick = onSubmit, // Llama handler
                enabled = canSubmit && !isSubmitting, // Usa parámetros
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) { // Usa parámetro
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrar")
                }
            }

            // Mensaje de error global
            if (errorMsg != null) { // Usa parámetro
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            // --- BOTÓN IR A LOGIN ---
            OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Ir a Login")
            }
        }
    }
}

