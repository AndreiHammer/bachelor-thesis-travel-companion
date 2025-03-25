package eu.ase.travelcompanionapp.user.presentation.settings

sealed interface SettingsAction {
    data object OnBackClick : SettingsAction
} 