package eu.ase.travelcompanionapp.hotel.presentation

import eu.ase.travelcompanionapp.hotel.domain.Hotel

private val hotels = (1..100).map {
    Hotel(
        hotelId = it.toString(),
        chainCode = "CHAIN$it",
        iataCode = "IATACODE$it",
        dupeId = it,
        name = "HOTEL$it",
        latitude = it.toDouble(),
        longitude = it.toDouble(),
        countryCode = "COUNTRY$it",
        amenities = arrayListOf("AMENITY$it"),
        rating = it,
        giataId = it,
        phone = "PHONE$it"
    )
}