package com.example.mynotes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

enum class ThemeOption {
    SYSTEM_DEFAULT,
    BLUE,
    GREEN,
    ORANGE,
    PURPLE
}

class ThemePreferences(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("selected_theme")

    val selectedTheme: Flow<ThemeOption> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_KEY] ?: ThemeOption.SYSTEM_DEFAULT.name
            try {
                ThemeOption.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeOption.SYSTEM_DEFAULT
            }
        }

    suspend fun setSelectedTheme(theme: ThemeOption) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}