package eu.ase.travelcompanionapp.user.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferredCurrency: Flow<String>
    val isDarkTheme: Flow<Boolean>

    suspend fun setPreferredCurrency(currency: String)
    suspend fun setDarkTheme(isDarkTheme: Boolean)
}