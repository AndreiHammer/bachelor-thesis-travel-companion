package eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.map
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.RemoteHotelDataSource
import eu.ase.travelcompanionapp.hotel.data.mappers.toHotel
import eu.ase.travelcompanionapp.hotel.data.mappers.toHotelOffer
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi

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
            when (result) {
                is Result.Error -> onResult(Result.Error(result.error))
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        onResult(Result.Error(DataError.Remote.NOT_FOUND))
                    } else {
                        onResult(Result.Success(result.data.map { it.toHotel() }))
                    }
                }
            }
        }
    }

    override suspend fun searchHotelsByLocation(
        latitude: Double,
        longitude: Double,
        radius: Int,
        amenities: String,
        rating: String,
        onResult: (Result<List<Hotel>, DataError>) -> Unit
    ) {
        remoteHotelDataSource.searchHotelsByLocation(latitude, longitude, radius, amenities, rating) { result ->
            when (result) {
                is Result.Error -> onResult(Result.Error(result.error))
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        onResult(Result.Error(DataError.Remote.NOT_FOUND))
                    } else {
                        onResult(Result.Success(result.data.map { it.toHotel() }))
                    }
                }
            }
        }
    }

    override suspend fun searchHotelOffers(
        hotelIds: String,
        checkInDate: String,
        checkOutDate: String,
        adults: String,
        bestRateOnly: Boolean,
        onResult: (Result<List<HotelOffer>, DataError.Remote>) -> Unit
    ) {
        remoteHotelDataSource.searchHotelOffers(
            hotelIds, 
            checkInDate, 
            checkOutDate, 
            adults,
            bestRateOnly
        ) { result ->
            onResult(result.map { hotelOffersDto ->
                hotelOffersDto.map { it.toHotelOffer() }
            })
        }
    }
}