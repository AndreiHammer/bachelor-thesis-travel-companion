package eu.ase.travelcompanionapp.hotel.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer

interface HotelRepositoryAmadeusApi {
    suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<Hotel>, DataError>) -> Unit
    )

    suspend fun searchHotelsByLocation(
        latitude: Double,
        longitude: Double,
        radius: Int,
        amenities: String,
        rating: String,
        onResult: (Result<List<Hotel>, DataError>) -> Unit
    )

    suspend fun searchHotelOffers(
        hotelIds: String,
        checkInDate: String,
        checkOutDate: String,
        adults: String,
        bestRateOnly: Boolean = false,
        onResult: (Result<List<HotelOffer>, DataError.Remote>) -> Unit
    )
}