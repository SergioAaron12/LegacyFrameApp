package com.example.legacyframeapp.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore (base de datos de preferencias)
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // Claves de almacenamiento
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_EMAIL = stringPreferencesKey("user_email")

        // Configuración UI
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val FONT_SCALE = floatPreferencesKey("font_scale")

        // Configuración Notificaciones
        val NOTIF_OFFERS = booleanPreferencesKey("notif_offers")
        val NOTIF_TRACKING = booleanPreferencesKey("notif_tracking")
        val NOTIF_CART = booleanPreferencesKey("notif_cart")
    }

    // --- FLOWS (Lectura en tiempo real para la UI) ---
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val authToken: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN] }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: false }
    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "system" }
    val accentColor: Flow<String> = context.dataStore.data.map { it[ACCENT_COLOR] ?: "#FF8B5C2A" }
    val fontScale: Flow<Float> = context.dataStore.data.map { it[FONT_SCALE] ?: 1.0f }

    val notifOffers: Flow<Boolean> = context.dataStore.data.map { it[NOTIF_OFFERS] ?: true }
    val notifTracking: Flow<Boolean> = context.dataStore.data.map { it[NOTIF_TRACKING] ?: true }
    val notifCart: Flow<Boolean> = context.dataStore.data.map { it[NOTIF_CART] ?: true }

    // --- FUNCIONES DE GUARDADO (SUSPEND) ---

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = loggedIn }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { it[USER_EMAIL] = email }
    }

    // --- LECTURA SEGURA (Para usar dentro de corrutinas del ViewModel) ---
    suspend fun getEmail(): String? {
        // Usamos firstOrNull() para obtener el valor una vez sin romper el flujo ni cerrar la app
        return context.dataStore.data.map { it[USER_EMAIL] }.firstOrNull()
    }

    // Configuración UI
    suspend fun setThemeMode(mode: String) { context.dataStore.edit { it[THEME_MODE] = mode } }
    suspend fun setAccentColor(color: String) { context.dataStore.edit { it[ACCENT_COLOR] = color } }
    suspend fun setFontScale(scale: Float) { context.dataStore.edit { it[FONT_SCALE] = scale } }

    suspend fun setNotifOffers(enabled: Boolean) { context.dataStore.edit { it[NOTIF_OFFERS] = enabled } }
    suspend fun setNotifTracking(enabled: Boolean) { context.dataStore.edit { it[NOTIF_TRACKING] = enabled } }
    suspend fun setNotifCart(enabled: Boolean) { context.dataStore.edit { it[NOTIF_CART] = enabled } }

    // --- LIMPIAR DATOS (LOGOUT) ---
    suspend fun clear() {
        context.dataStore.edit {
            it.remove(IS_LOGGED_IN)
            it.remove(AUTH_TOKEN)
            it.remove(USER_EMAIL)
            // No borramos preferencias de tema para mantener la experiencia
        }
    }
}