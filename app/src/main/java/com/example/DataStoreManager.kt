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
    }

    val reminderTime = context.dataStore.data.map { prefs ->
        Pair(prefs[HOUR_KEY] ?: 18, prefs[MINUTE_KEY] ?: 0)
    }

    val isEnabled = context.dataStore.data.map { prefs ->
        prefs[ENABLED_KEY] ?: false
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
}
