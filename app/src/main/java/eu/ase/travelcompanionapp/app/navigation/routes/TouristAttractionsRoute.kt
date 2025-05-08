package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface TouristAttractionsRoute {
    @Serializable
    data object TouristAttractionsGraph : TouristAttractionsRoute

    @Serializable
    data class TouristAttractionsList(
        val latitude: Double,
        val longitude: Double
    ) : TouristAttractionsRoute

    @Serializable
    data class TouristAttractionDetails(
        val attractionId: String
    ) : TouristAttractionsRoute
} 