package eu.ase.travelcompanionapp.user.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.ui.ThemeManager
import eu.ase.travelcompanionapp.user.domain.model.Currency
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferences: UserPreferencesRepository,
    private val themeManager: ThemeManager,
    private val navController: NavHostController
) : ViewModel() {
    
    private val _currencies = MutableStateFlow<List<Currency>>(emptyList())
    val currencies: StateFlow<List<Currency>> = _currencies.asStateFlow()
    
    val selectedCurrency: StateFlow<String> = userPreferences.preferredCurrency
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )
        
    val isDarkTheme: StateFlow<Boolean> = themeManager.isDarkTheme
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    
    init {
        loadCurrencies()
    }
    
    private fun loadCurrencies() {
        _currencies.value = Currency.getAll()
    }
    
    fun updatePreferredCurrency(currencyCode: String) {
        viewModelScope.launch {
            userPreferences.setPreferredCurrency(currencyCode)
        }
    }
    
    fun updateThemePreference(isDarkTheme: Boolean) {
        themeManager.setDarkTheme(isDarkTheme)
    }
    
    fun handleAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnBackClick -> {
                navController.popBackStack()
            }
        }
    }
} 