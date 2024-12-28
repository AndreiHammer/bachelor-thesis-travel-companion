package eu.ase.travelcompanionapp.hotel.data.placesApi

import coil3.Bitmap
import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi

class PlacesHotelRepository(private val placesApiService: PlacesApiService):
    HotelRepositoryPlacesApi {
    override suspend fun getHotelDetails(
        hotelName: String,
        country: String,
        onResult: (Result<Pair<Hotel, List<Bitmap>>, DataError>) -> Unit
    ) {
        placesApiService.getHotelDetails(hotelName, country) { result ->
            when (result) {
                is Result.Error -> onResult(Result.Error(DataError.Remote.UNKNOWN))
                is Result.Success -> onResult(result)
            }
        }
    }
}
