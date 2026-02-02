package com.example.legacyframeapp

import com.example.legacyframeapp.domain.validation.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    // --- PRUEBA 1: Email ---
    @Test
    fun `validateEmail retorna error si esta vacio`() {
        // Ejecutar (When)
        val result = validateEmail("")
        // Verificar (Then)
        assertEquals("El correo es obligatorio", result)
    }

    @Test
    fun `validateEmail retorna null si es correcto`() {
        val result = validateEmail("test@duoc.cl")
        assertNull(result) // Null significa que no hubo error
    }

    // --- PRUEBA 2: RUT ---
    @Test
    fun `validateRut detecta letras`() {
        val result = validateRut("1234567K")
        assertEquals("Solo números (sin puntos ni guión)", result)
    }

    @Test
    fun `validateRut acepta rut valido`() {
        val result = validateRut("12345678")
        assertNull(result)
    }

    // --- PRUEBA 3: Contraseña ---
    @Test
    fun `validateStrongPassword pide mayuscula`() {
        val result = validateStrongPassword("clave123!")
        assertEquals("Debe incluir una mayúscula", result)
    }

    @Test
    fun `validateStrongPassword acepta clave segura`() {
        val result = validateStrongPassword("ClaveSegura1!")
        assertNull(result)
    }
}