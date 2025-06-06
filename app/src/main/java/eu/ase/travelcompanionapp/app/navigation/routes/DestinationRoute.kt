package eu.ase.travelcompanionapp.app.navigation.routes

import kotlinx.serialization.Serializable

sealed interface DestinationRoute {
    @Serializable
    data object DestinationGraph : DestinationRoute
    
    @Serializable
    data object Recommendations : DestinationRoute

    @Serializable
    data object DestinationList : DestinationRoute
    
    @Serializable
    data class DestinationDetail(
        val city: String,
        val country: String,
        val iataCode: String,
        val continent: String,
        val latitude: Double,
        val longitude: Double,
        val description: String,
        val matchReasons: List<String>,
        val bestFor: List<String>,
        val seasonScore: Int,
        val budgetLevel: String,
        val popularAttractions: List<String>,
    ) : DestinationRoute
} 