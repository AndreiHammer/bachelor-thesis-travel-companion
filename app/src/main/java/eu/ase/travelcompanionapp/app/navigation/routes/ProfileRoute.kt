package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface ProfileRoute {
    @Serializable
    data object ProfileGraph : ProfileRoute
    @Serializable
    data object Profile : ProfileRoute
    @Serializable
    data object Settings : ProfileRoute
}