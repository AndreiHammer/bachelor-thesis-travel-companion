package eu.ase.travelcompanionapp.hotel.domain

import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result

interface HotelRepositoryAmadeusApi {
    suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<Hotel>, DataError>) -> Unit
    )
}