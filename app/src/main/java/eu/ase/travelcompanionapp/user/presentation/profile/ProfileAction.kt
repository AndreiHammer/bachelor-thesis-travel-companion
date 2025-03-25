package eu.ase.travelcompanionapp.user.presentation.profile

sealed interface ProfileAction {
    data object SignedOut : ProfileAction
    data object AccountDeleted : ProfileAction
    data object OnBackClick: ProfileAction
    data object OnSettingsClick : ProfileAction
}