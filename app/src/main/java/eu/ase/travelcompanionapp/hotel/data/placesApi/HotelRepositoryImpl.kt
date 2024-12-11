package eu.ase.travelcompanionapp.hotel.data.placesApi

import coil3.Bitmap
import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryPlacesApi

class HotelRepositoryImpl(private val placesApiService: PlacesApiService): HotelRepositoryPlacesApi {
    override fun getHotelDetails(
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
