package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface AuthRoute {
    @Serializable
    data object AuthGraph : AuthRoute
    @Serializable
    data object Start : AuthRoute
    @Serializable
    data object Login : AuthRoute
    @Serializable
    data object SignUp : AuthRoute
}