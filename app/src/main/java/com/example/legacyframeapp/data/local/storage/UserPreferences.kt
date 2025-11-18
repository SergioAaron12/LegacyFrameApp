package com.example.legacyframeapp.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//extension o elemento para obtener y manipular el Data Store
val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences (private val context: Context){
    //clave boolean para manejar el estado del login
    private val isLoggedInKey = booleanPreferencesKey("is_logged_key")
    private val darkModeKey = booleanPreferencesKey("dark_mode_key")
    private val avatarTypeKey = stringPreferencesKey("avatar_type_key") // "male" | "female"
    // Nuevas claves de configuración
    private val themeModeKey = stringPreferencesKey("theme_mode_key") // light | dark | system
    private val accentColorKey = stringPreferencesKey("accent_color_key") // hex ARGB e.g. #FF8B5C2A
    private val fontScaleKey = stringPreferencesKey("font_scale_key") // almacenar como String para simplicidad
    private val notifOffersKey = booleanPreferencesKey("notif_offers_key")
    private val notifTrackingKey = booleanPreferencesKey("notif_tracking_key")
    private val notifCartKey = booleanPreferencesKey("notif_cart_key")
    private val languageKey = stringPreferencesKey("language_key") // es | en | system

    //funcion para setear el valor de la variable
    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    suspend fun setDarkMode(enabled: Boolean){
        context.dataStore.edit { prefs ->
            prefs[darkModeKey] = enabled
        }
    }

    suspend fun setAvatarType(type: String){
        context.dataStore.edit { prefs ->
            prefs[avatarTypeKey] = type
        }
    }
    suspend fun setThemeMode(mode: String){
        context.dataStore.edit { prefs -> prefs[themeModeKey] = mode }
    }
    suspend fun setAccentColor(hex: String){
        context.dataStore.edit { prefs -> prefs[accentColorKey] = hex }
    }
    suspend fun setFontScale(scale: Float){
        context.dataStore.edit { prefs -> prefs[fontScaleKey] = scale.toString() }
    }
    suspend fun setNotifOffers(enabled: Boolean){ context.dataStore.edit { it[notifOffersKey] = enabled } }
    suspend fun setNotifTracking(enabled: Boolean){ context.dataStore.edit { it[notifTrackingKey] = enabled } }
    suspend fun setNotifCart(enabled: Boolean){ context.dataStore.edit { it[notifCartKey] = enabled } }
    suspend fun setLanguage(code: String){ context.dataStore.edit { it[languageKey] = code } }
    //exposicion del dataStore
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[darkModeKey] ?: false
        }

    // Avatar seleccionado por el usuario (por defecto "male" si no existe)
    val avatarType: Flow<String> = context.dataStore.data
        .map { prefs ->
            prefs[avatarTypeKey] ?: "male"
        }

    val themeMode: Flow<String> = context.dataStore.data
        .map { it[themeModeKey] ?: "system" }
    val accentColor: Flow<String> = context.dataStore.data
        .map { it[accentColorKey] ?: "#FF8B5C2A" }
    val fontScale: Flow<Float> = context.dataStore.data
        .map { prefs -> prefs[fontScaleKey]?.toFloatOrNull() ?: 1.0f }
    val notifOffers: Flow<Boolean> = context.dataStore.data.map { it[notifOffersKey] ?: true }
    val notifTracking: Flow<Boolean> = context.dataStore.data.map { it[notifTrackingKey] ?: true }
    val notifCart: Flow<Boolean> = context.dataStore.data.map { it[notifCartKey] ?: true }
    val language: Flow<String> = context.dataStore.data.map { it[languageKey] ?: "system" }
}
