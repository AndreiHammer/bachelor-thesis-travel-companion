package eu.ase.travelcompanionapp.user.presentation.profile

sealed interface ProfileAction {
    data object OnBackClick: ProfileAction
    data object OnSettingsClick : ProfileAction
    data object OnSignOutClick : ProfileAction
    data object ConfirmSignOut : ProfileAction
    data object OnDeleteAccountClick : ProfileAction
    data object ConfirmDeleteAccount : ProfileAction
    data object OnViewBookingHistoryClick : ProfileAction
}