package eu.ase.travelcompanionapp.user.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferredCurrency: Flow<String>

    suspend fun setPreferredCurrency(currency: String)
}