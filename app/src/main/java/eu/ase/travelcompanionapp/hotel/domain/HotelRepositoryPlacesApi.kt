package eu.ase.travelcompanionapp.hotel.domain

import coil3.Bitmap
import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result

interface HotelRepositoryPlacesApi {
    fun getHotelDetails(
        hotelName: String,
        country: String,
        onResult: (Result<Pair<Hotel, List<Bitmap>>, DataError>) -> Unit
    )
}