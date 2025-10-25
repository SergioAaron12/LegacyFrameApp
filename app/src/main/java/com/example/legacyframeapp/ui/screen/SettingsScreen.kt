package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import com.example.legacyframeapp.ui.components.AppButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.legacyframeapp.ui.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun SettingsScreenVm(
    vm: AuthViewModel,
    onGoPurchases: () -> Unit,
    onGoTerms: () -> Unit,
    onGoContact: () -> Unit,
    onBack: () -> Unit = {}
) {
    val darkMode by vm.darkMode.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    SettingsScreen(
        darkMode = darkMode,
        onDarkModeChange = { enabled -> scope.launch { vm.setDarkMode(enabled) } },
        onGoPurchases = onGoPurchases,
        onGoTerms = onGoTerms,
        onGoContact = onGoContact
    )
}

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onGoPurchases: () -> Unit,
    onGoTerms: () -> Unit,
    onGoContact: () -> Unit
) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            Text("Configuraciones", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.padding(8.dp))
            ListItem(
                headlineContent = { Text("Modo oscuro") },
                trailingContent = {
                    Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            AppButton(modifier = Modifier.fillMaxWidth(), onClick = onGoContact) { Text("Contacto") }
            AppButton(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), onClick = onGoPurchases) { Text("Ver tus compras") }
            AppButton(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), onClick = onGoTerms) { Text("Términos y condiciones") }
        }
    }
}
