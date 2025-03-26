package eu.ase.travelcompanionapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import eu.ase.travelcompanionapp.ui.theme.TravelCompanionAppTheme
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ThemeManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val isDarkTheme: Flow<Boolean> = userPreferencesRepository.isDarkTheme

    fun setDarkTheme(isDarkTheme: Boolean) {
        coroutineScope.launch {
            userPreferencesRepository.setDarkTheme(isDarkTheme)
        }
    }

    @Composable
    fun ApplyTheme(content: @Composable () -> Unit) {
        val isDarkTheme by userPreferencesRepository.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())
        
        TravelCompanionAppTheme(
            darkTheme = isDarkTheme,
            content = content
        )
    }
} 