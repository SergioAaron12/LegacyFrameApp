package com.example.legacyframeapp.domain.validation

// Eliminamos dependencias de Android para que sea testearle
// private const val EMAIL_PATTERN = ...

fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El correo es obligatorio"
    if (email.length < 8) return "Debe tener al menos 8 caracteres"
    // Regex estándar para email
    val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
    return if (!regex.matches(email)) "Formato de correo inválido" else null
}

fun validateNameLettersOnly(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

fun validateApellido(apellido: String): String? {
    if (apellido.isNotBlank() && !Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$").matches(apellido)) {
        return "Solo letras y espacios"
    }
    return null
}

fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo números"
    if (phone.length != 9) return "Debe tener 9 dígitos"
    return null
}

fun validateRut(rut: String): String? {
    if (rut.isBlank()) return "El RUT es obligatorio"
    if (!rut.all { it.isDigit() }) return "Solo números (sin puntos ni guión)"
    if (rut.length < 7 || rut.length > 8) return "RUT inválido (7-8 dígitos)"
    return null
}

fun validateDv(dv: String, rut: String): String? {
    if (dv.isBlank()) return "El DV es obligatorio"
    if (dv.length != 1) return "DV inválido (1 carácter)"
    if (!dv.matches(Regex("[0-9Kk]"))) return "DV inválido (número o K)"
    if (validateRut(rut) != null) return null

    val dvCalculado = calcularDv(rut)
    return if (dv.uppercase() == dvCalculado) null else "DV incorrecto"
}

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
        return ""
    }
}

fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 8) return "Mínimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe incluir una mayúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if (pass.contains(' ')) return "No debe contener espacios"
    return null
}

fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}