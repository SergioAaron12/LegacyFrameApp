package com.example.legacyframeapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TermsScreen(onBack: () -> Unit = {}) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            Text("Términos y condiciones", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Al utilizar Legacy Frames aceptas nuestros términos de uso y políticas de privacidad. " +
                        "Los precios pueden variar sin previo aviso. Las imágenes son referenciales. " +
                        "Para más información o consultas contáctanos por WhatsApp o teléfono.",
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
