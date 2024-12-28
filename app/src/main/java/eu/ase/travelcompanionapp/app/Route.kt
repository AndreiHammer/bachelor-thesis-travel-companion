package eu.ase.travelcompanionapp.app

import kotlinx.serialization.Serializable

sealed interface Route {
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