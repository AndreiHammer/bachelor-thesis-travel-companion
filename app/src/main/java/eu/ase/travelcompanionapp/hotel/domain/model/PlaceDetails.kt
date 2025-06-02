package eu.ase.travelcompanionapp.hotel.domain.model

import coil3.Bitmap

data class PlaceDetails(
    val hotel: Hotel,
    var photos: List<Bitmap>,
    val rating: Double,
    val reviews: List<Review>
)
