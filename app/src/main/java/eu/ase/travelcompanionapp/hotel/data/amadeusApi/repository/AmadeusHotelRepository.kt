package eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository

import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.core.domain.map
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.RemoteHotelDataSource
import eu.ase.travelcompanionapp.hotel.data.mappers.toHotel
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryAmadeusApi

class AmadeusHotelRepository(
    private val remoteHotelDataSource: RemoteHotelDataSource
) : HotelRepositoryAmadeusApi {

    override suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<Hotel>, DataError>) -> Unit
    ) {
        remoteHotelDataSource.searchHotelsByCity(city, amenities, rating) { result ->
            onResult(result.map { hotelDto ->
                hotelDto.map {
                    it.toHotel()
                }
            })
        }
    }
}