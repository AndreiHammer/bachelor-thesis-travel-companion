package eu.ase.travelcompanionapp.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Root: Route

    @Serializable
    data object Splash: Route

    @Serializable
    data object AuthGraph: Route

    @Serializable
    data object Start: Route

    @Serializable
    data object Login: Route

    @Serializable
    data object SignUp: Route

    @Serializable
    data object Profile: Route

    @Serializable
    data object LocationSearch: Route

    @Serializable
    data object HotelGraph: Route

    @Serializable
    data class HotelListCity(val city: String): Route

    @Serializable
    data class HotelListLocation(val latitute: Double, val longitude: Double, val range: Float): Route

    @Serializable
    data class HotelDetail(val id: String): Route

    @Serializable
    data object MapSearch: Route

}