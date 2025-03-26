package eu.ase.travelcompanionapp.user.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import eu.ase.travelcompanionapp.auth.data.auth.AuthManager
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferences(
    private val context: Context,
): UserPreferencesRepository {
    private val authManager = AuthManager()

    companion object {
        private const val PREFERRED_CURRENCY_KEY = "preferred_currency"
        private const val DARK_THEME_KEY = "dark_theme"
    }

    private fun getPreferredCurrencyKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$PREFERRED_CURRENCY_KEY"
        } else {
            "${userId}_$PREFERRED_CURRENCY_KEY"
        }
    }
    
    private fun getDarkThemeKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$DARK_THEME_KEY"
        } else {
            "${userId}_$DARK_THEME_KEY"
        }
    }
    
    override val preferredCurrency: Flow<String> = context.userPreferencesDataStore.data.map { preferences ->
        val key = stringPreferencesKey(getPreferredCurrencyKey())
        preferences[key] ?: ""
    }
    
    override val isDarkTheme: Flow<Boolean> = context.userPreferencesDataStore.data.map { preferences ->
        val key = booleanPreferencesKey(getDarkThemeKey())
        preferences[key] ?: false
    }
    
    override suspend fun setPreferredCurrency(currency: String) {
        val key = stringPreferencesKey(getPreferredCurrencyKey())
        context.userPreferencesDataStore.edit { preferences ->
            preferences[key] = currency
        }
    }
    
    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        val key = booleanPreferencesKey(getDarkThemeKey())
        context.userPreferencesDataStore.edit { preferences ->
            preferences[key] = isDarkTheme
        }
    }
} 