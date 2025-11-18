package com.example.legacyframeapp.domain.validation

import android.util.Patterns // Usamos el patrón estándar de Android para emails

// Valida que el email no esté vacío y cumpla patrón de email
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El correo es obligatorio"
    if (email.length < 8) return "Debe tener al menos 8 caracteres"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de correo inválido" else null
}

// Valida que el nombre contenga solo letras y espacios (sin números)
fun validateNameLettersOnly(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

// Valida apellido (opcional, pero si se ingresa, solo letras y espacios)
fun validateApellido(apellido: String): String? {
    if (apellido.isNotBlank() && !Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$").matches(apellido)) {
        return "Solo letras y espacios"
    }
    return null // Es válido si está vacío o si cumple el formato
}


// Valida que el teléfono tenga solo dígitos y una longitud razonable
fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio" // Ahora es obligatorio
    if (!phone.all { it.isDigit() }) return "Solo números"
    // Ajusta la longitud según necesites (ej: 9 para Chile sin +56)
    if (phone.length != 9) return "Debe tener 9 dígitos"
    return null
}

// --- Valida formato básico del RUT (números, longitud) ---
fun validateRut(rut: String): String? {
    if (rut.isBlank()) return "El RUT es obligatorio"
    if (!rut.all { it.isDigit() }) return "Solo números (sin puntos ni guión)"
    if (rut.length < 7 || rut.length > 8) return "RUT inválido (7-8 dígitos)"
    return null
}

// --- Valida el Dígito Verificador (DV) ---
fun validateDv(dv: String, rut: String): String? {
    if (dv.isBlank()) return "El DV es obligatorio"
    if (dv.length != 1) return "DV inválido (1 carácter)"
    if (!dv.matches(Regex("[0-9Kk]"))) return "DV inválido (número o K)"

    // Si el RUT tiene errores, no podemos validar el DV aún
    if (validateRut(rut) != null) return null // Espera a que el RUT sea válido

    val dvCalculado = calcularDv(rut)
    return if (dv.uppercase() == dvCalculado) null else "DV incorrecto"
}

// --- Función auxiliar para calcular el DV ---
private fun calcularDv(rut: String): String {
    try {
        var rutLimpio = rut.toInt()
        var m = 0
        var s = 1
        while (rutLimpio != 0) {
            s = (s + rutLimpio % 10 * (9 - m++ % 6)) % 11
            rutLimpio /= 10
        }
        return if (s != 0) (s + 47).toChar().toString() else "K"
    } catch (e: NumberFormatException) {
        return "" // Devuelve vacío si el RUT no es un número válido
    }
}
// -----------------------------------------------------------

// Valida seguridad de la contraseña
fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 8) return "Mínimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe incluir una mayúscula"
    // Podrías quitar la minúscula si no es requisito estricto
    // if (!pass.any { it.isLowerCase() }) return "Debe incluir una minúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if (pass.contains(' ')) return "No debe contener espacios"
    return null
}

// Valida que la confirmación coincida con la contraseña
fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}
