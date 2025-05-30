package eu.ase.travelcompanionapp.hotel.domain.model

data class Review(
    val authorName: String,
    val photoUri: String?,
    val rating: Double,
    val text: String,
    val time: String,
    val relativeTime: String
) 