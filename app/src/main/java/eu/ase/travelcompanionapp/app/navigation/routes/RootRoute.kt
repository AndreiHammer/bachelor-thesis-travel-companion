package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface RootRoute {
    @Serializable
    data object RootGraph : RootRoute
    @Serializable
    data object Splash : RootRoute
}
