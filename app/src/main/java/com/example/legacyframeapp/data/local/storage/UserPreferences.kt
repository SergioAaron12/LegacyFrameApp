package com.example.legacyframeapp.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//extension o elemento para obtener y manipular el Data Store
val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences (private val context: Context){
    //clave boolean para manejar el estado del login
    private val isLoggedInKey = booleanPreferencesKey("is_logged_key")

    //funcion para setear el valor de la variable
    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }
    //exposicion del dataStore
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }
}
