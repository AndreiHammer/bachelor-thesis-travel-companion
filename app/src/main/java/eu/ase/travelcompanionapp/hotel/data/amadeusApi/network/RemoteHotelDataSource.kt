package eu.ase.travelcompanionapp.hotel.data.amadeusApi.network

import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelDto

interface RemoteHotelDataSource {
    suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    )
}