package com.example

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "beer_reminder_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val HOUR_KEY = intPreferencesKey("reminder_hour")
        val MINUTE_KEY = intPreferencesKey("reminder_minute")
        val ENABLED_KEY = booleanPreferencesKey("reminder_enabled")
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val GLASS_TYPE_KEY = androidx.datastore.preferences.core.stringPreferencesKey("beer_glass_type")
        val LANGUAGE_KEY = androidx.datastore.preferences.core.stringPreferencesKey("app_language")
    }

    val language = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.code
    }

    val reminderTime = context.dataStore.data.map { prefs ->
        Pair(prefs[HOUR_KEY] ?: 18, prefs[MINUTE_KEY] ?: 0)
    }

    val isEnabled = context.dataStore.data.map { prefs ->
        prefs[ENABLED_KEY] ?: false
    }

    val isDarkTheme = context.dataStore.data.map { prefs ->
        prefs[DARK_THEME_KEY] ?: false
    }

    val glassType = context.dataStore.data.map { prefs ->
        val name = prefs[GLASS_TYPE_KEY] ?: BeerGlassType.PINT.name
        try {
            BeerGlassType.valueOf(name)
        } catch (e: Exception) {
            BeerGlassType.PINT
        }
    }

    suspend fun saveReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[HOUR_KEY] = hour
            prefs[MINUTE_KEY] = minute
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ENABLED_KEY] = enabled
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = isDark
        }
    }

    suspend fun setGlassType(type: BeerGlassType) {
        context.dataStore.edit { prefs ->
            prefs[GLASS_TYPE_KEY] = type.name
        }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = lang.code
        }
    }
}
