package eu.ase.travelcompanionapp.hotel.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.PlaceDetails

interface HotelRepositoryPlacesApi {
    suspend fun getHotelDetails(
        hotelName: String,
        country: String,
        onResult: (Result<PlaceDetails, DataError>) -> Unit
    )
}