package eu.ase.travelcompanionapp.recommendation.domain.model

data class Destination(
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
    val popularAttractions: List<String>
)