package eu.ase.travelcompanionapp.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object LocationSearch: Route

    @Serializable
    data object HotelGraph: Route

    @Serializable
    data class HotelList(val city: String): Route

    @Serializable
    data class HotelDetail(val id: String): Route
}