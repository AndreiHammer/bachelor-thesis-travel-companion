package eu.ase.travelcompanionapp.hotel.domain.repository

import eu.ase.travelcompanionapp.hotel.domain.model.BookingDetails
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface FavouriteHotelRepository {
    fun getFavouriteHotels(): Flow<List<Hotel>>

    suspend fun isFavourite(hotelId: String): Boolean

    suspend fun addFavourite(hotel: Hotel, checkInDate: String? = null,
                            checkOutDate: String? = null, adults: Int? = null)

    suspend fun removeFavourite(hotelId: String)

    suspend fun getBookingDetails(hotelId: String): BookingDetails?
}