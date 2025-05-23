package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface TouristAttractionsRoute {
    @Serializable
    data object TouristAttractionsGraph : TouristAttractionsRoute

    @Serializable
    data object TouristAttractionsList : TouristAttractionsRoute

    @Serializable
    data object TouristAttractionDetails : TouristAttractionsRoute
} 