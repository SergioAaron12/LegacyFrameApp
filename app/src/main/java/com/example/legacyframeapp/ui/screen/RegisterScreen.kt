package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.background // <-- MODIFICADO
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
import com.example.legacyframeapp.ui.components.AppButton
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

// --- Stateful Composable (SIN CAMBIOS) ---
@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {
    val state by vm.register.collectAsStateWithLifecycle()
    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearRegisterResult()
            onRegisteredNavigateLogin()
        }
    }
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
        onNombreChange = vm::onRegisterNombreChange,
        onApellidoChange = vm::onRegisterApellidoChange,
        onRutChange = vm::onRegisterRutChange,
        onDvChange = vm::onRegisterDvChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onRegisterPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onRegisterConfirmChange,
        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}

// --- Stateless/Presentational Composable (MODIFICADO) ---
@Composable
private fun RegisterScreen(
    nombre: String,
    apellido: String,
    rut: String,
    dv: String,
    email: String,
    phone: String,
    pass: String,
    confirm: String,
    nombreError: String?,
    apellidoError: String?,
    rutError: String?,
    dvError: String?,
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
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
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // --- NUEVA ESTRUCTURA ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            // =====> CAMBIO 1: Fondo de color café claro (de tu tema) <=====
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp), // Padding para que la tarjeta no toque los bordes
        contentAlignment = Alignment.Center
    ) {
        // 1. Imagen de Fondo
        // --- SE ELIMINÓ EL COMPOSABLE Image(...) ---

        // 2. Tarjeta de Formulario
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            // =====> CAMBIO 2: Color blanco opaco (de tu tema) <=====
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp
        ) {
            // 3. Contenido del Formulario (la columna que ya tenías)
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Crear Cuenta", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))

                // ... (Todos tus OutlinedTextField de Nombre, Apellido, RUT, etc., van aquí) ...
                // (No los pego todos para no hacer la respuesta gigante,
                // pero van exactamente como los tenías, dentro de esta Column)

                // --- CAMPO NOMBRE ---
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre") },
                    isError = nombreError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nombreError != null) {
                    Text(nombreError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))

                // --- CAMPO APELLIDO ---
                OutlinedTextField(
                    value = apellido,
                    onValueChange = onApellidoChange,
                    label = { Text("Apellido (Opcional)") },
                    isError = apellidoError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (apellidoError != null) {
                    Text(apellidoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))

                // --- CAMPOS RUT y DV ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(0.75f)) {
                        OutlinedTextField(
                            value = rut,
                            onValueChange = onRutChange,
                            label = { Text("RUT (sin DV)") },
                            isError = rutError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (rutError != null) {
                            Text(rutError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Column(modifier = Modifier.weight(0.25f)) {
                        OutlinedTextField(
                            value = dv,
                            onValueChange = onDvChange,
                            label = { Text("DV") },
                            isError = dvError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (dvError != null) {
                            Text(dvError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // --- CAMPO EMAIL ---
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Correo Electrónico") },
                    isError = emailError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))

                // --- CAMPO TELÉFONO ---
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Teléfono") },
                    isError = phoneError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                if (phoneError != null) {
                    Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))

                // --- CAMPO CONTRASEÑA ---
                OutlinedTextField(
                    value = pass,
                    onValueChange = onPassChange,
                    label = { Text("Contraseña") },
                    isError = passError != null,
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
                if (passError != null) {
                    Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))

                // --- CAMPO CONFIRMAR CONTRASEÑA ---
                OutlinedTextField(
                    value = confirm,
                    onValueChange = onConfirmChange,
                    label = { Text("Confirmar Contraseña") },
                    isError = confirmError != null,
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
                if (confirmError != null) {
                    Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(16.dp))

                // --- BOTÓN REGISTRAR ---
                AppButton(
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Creando cuenta...")
                    } else {
                        Text("Registrar")
                    }
                }

                // Mensaje de error global
                if (errorMsg != null) {
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
}