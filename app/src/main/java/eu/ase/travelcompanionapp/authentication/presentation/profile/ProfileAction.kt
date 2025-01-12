package eu.ase.travelcompanionapp.authentication.presentation.profile

sealed interface ProfileAction {
    data object SignedOut : ProfileAction
    data object AccountDeleted : ProfileAction
    data object OnBackClick: ProfileAction
}