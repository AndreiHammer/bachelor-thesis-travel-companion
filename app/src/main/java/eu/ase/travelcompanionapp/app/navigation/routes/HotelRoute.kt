package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface HotelRoute {
    @Serializable
    data object HotelGraph : HotelRoute
    @Serializable
    data object LocationSearch : HotelRoute
    @Serializable
    data object MapSearch : HotelRoute

    @Serializable
    data class HotelListCity(val city: String, val amenities: String, val rating: String) : HotelRoute

    @Serializable
    data class HotelListLocation(
        val latitude: Double,
        val longitude: Double,
        val range: Float
    ) : HotelRoute

    @Serializable
    data class HotelDetail(val id: String) : HotelRoute
}