package eu.ase.travelcompanionapp.hotel.data.placesApi

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.PlaceDetails
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi

class PlacesHotelRepository(private val placesApiService: PlacesApiService):
    HotelRepositoryPlacesApi {
    override suspend fun getHotelDetails(
        hotelName: String,
        country: String,
        onResult: (Result<PlaceDetails, DataError>) -> Unit
    ) {
        placesApiService.getHotelDetails(hotelName, country, onResult)
    }
}
