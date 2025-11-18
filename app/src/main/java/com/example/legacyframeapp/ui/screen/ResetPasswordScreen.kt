package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.legacyframeapp.ui.components.AppButton
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel

@Composable
fun ResetPasswordScreenVm(
    vm: AuthViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val state = vm.resetPassword.collectAsStateWithLifecycle().value

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearResetPasswordState()
            onSuccess()
        }
    }

    ResetPasswordScreen(
        email = state.email,
        newPass = state.newPass,
        confirm = state.confirm,
        emailError = state.emailError,
        passError = state.passError,
        confirmError = state.confirmError,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = vm::onResetEmailChange,
        onNewPassChange = vm::onResetNewPassChange,
        onConfirmChange = vm::onResetConfirmChange,
        onSubmit = vm::submitResetPassword,
        onBack = onBack
    )
}

@Composable
fun ResetPasswordScreen(
    email: String,
    newPass: String,
    confirm: String,
    emailError: String?,
    passError: String?,
    confirmError: String?,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onNewPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Restablecer contraseña", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo registrado") },
                singleLine = true,
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth()
            )
            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = newPass,
                onValueChange = onNewPassChange,
                label = { Text("Nueva contraseña") },
                singleLine = true,
                isError = passError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = onConfirmChange,
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                isError = confirmError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            confirmError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(Modifier.height(16.dp))

            AppButton(onClick = onSubmit, enabled = !isSubmitting, modifier = Modifier.fillMaxWidth()) {
                if (isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text("Restablecer")
                }
            }

            errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
                Text("Volver")
            }
        }
    }
}
