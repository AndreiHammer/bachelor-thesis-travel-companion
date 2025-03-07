package eu.ase.travelcompanionapp.hotel.domain.repository

import coil3.Bitmap
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

interface HotelRepositoryPlacesApi {
    suspend fun getHotelDetails(
        hotelName: String,
        country: String,
        onResult: (Result<Pair<Hotel, List<Bitmap>>, DataError>) -> Unit
    )
}