package com.example.legacyframeapp.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

fun formatWithThousands(digits: String): String {
    if (digits.isBlank()) return ""
    val chars = digits.toCharArray().reversed()
    val out = StringBuilder()
    for (i in chars.indices) {
        if (i > 0 && i % 3 == 0) out.append('.')
        out.append(chars[i])
    }
    return out.reverse().toString()
}

class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) {
            // No aplicar transformación ni mapeo cuando el texto es vacío
            return TransformedText(AnnotatedString(original), OffsetMapping.Identity)
        }
        val formatted = formatWithThousands(original)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Recorremos el texto transformado contando dígitos hasta alcanzar 'offset'
                var idx = 0
                var digitsSeen = 0
                while (idx < formatted.length) {
                    if (formatted[idx] != '.') {
                        if (digitsSeen == offset) break
                        digitsSeen++
                    }
                    idx++
                }
                return idx
            }
            override fun transformedToOriginal(offset: Int): Int {
                var digitsSeen = 0
                var i = 0
                while (i < formatted.length && i < offset) {
                    if (formatted[i] != '.') digitsSeen++
                    i++
                }
                // Asegura que el offset original esté en rango [0, original.length]
                return digitsSeen.coerceIn(0, original.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
