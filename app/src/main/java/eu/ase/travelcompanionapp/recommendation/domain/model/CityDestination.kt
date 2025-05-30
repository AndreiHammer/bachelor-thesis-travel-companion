package eu.ase.travelcompanionapp.recommendation.domain.model

data class CityDestination(
    val cityName: String,
    val iataCode: String,
    val countryCode: String,
    val country: String
)
