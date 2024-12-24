package eu.ase.travelcompanionapp.app

import eu.ase.travelcompanionapp.hotel.domain.Hotel
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object HotelGraph: Route

    @Serializable
    data object HotelList: Route

    @Serializable
    data class HotelDetail(val id: String): Route
}