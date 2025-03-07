package eu.ase.travelcompanionapp.hotel.data.amadeusApi.network

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelOffersDto

interface RemoteHotelDataSource {
    suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    )

    suspend fun searchHotelsByLocation(
        latitude: Double,
        longitude: Double,
        radius: Int,
        amenities: String,
        rating: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    )

    suspend fun searchHotelOffers(
        hotelIds: String,
        checkInDate: String,
        checkOutDate: String,
        adults: String,
        onResult: (Result<List<HotelOffersDto>, DataError.Remote>) -> Unit
    )
}