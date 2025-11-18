package com.example.legacyframeapp.ui.screen

// Importaciones (Image, ContentScale, painterResource ya no son necesarias aquí)
import androidx.compose.foundation.background // <-- MODIFICADO: Importa background
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
fun LoginScreenVm(
    vm: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit,
    onGoResetPassword: () -> Unit = {}
) {
    val state by vm.login.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) { // <-- Añadido LaunchedEffect para manejar navegación
        if (state.success) {
            vm.clearLoginResult()
            onLoginOkNavigateHome()
        }
    }

    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = vm::onLoginEmailChange,
        onPassChange = vm::onLoginPassChange,
        onSubmit = vm::submitLogin,
        onGoRegister = onGoRegister,
        onGoResetPassword = onGoResetPassword
    )
}

// --- Stateless/Presentational Composable (MODIFICADO) ---
@Composable
private fun LoginScreen(
    email: String,
    pass: String,
    emailError: String?,
    passError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit,
    onGoResetPassword: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // --- ESTRUCTURA MODIFICADA ---
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
                .fillMaxWidth(0.9f), // Ocupa el 90% del ancho
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
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))

                // --- EMAIL ---
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Correo") },
                    singleLine = true,
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))

                // --- PASSWORD ---
                OutlinedTextField(
                    value = pass,
                    onValueChange = onPassChange,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    isError = passError != null,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (passError != null) {
                    Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(16.dp))

                // --- BOTÓN ENTRAR ---
                            AppButton(
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Validando...")
                    } else {
                        Text("Entrar")
                    }
                }

                if (errorMsg != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorMsg, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))

                // --- BOTÓN IR A REGISTRO ---
                OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear cuenta")
                }

                TextButton(onClick = onGoResetPassword) {
                    Text("¿Olvidaste tu contraseña?")
                }
            }
        }
    }
}