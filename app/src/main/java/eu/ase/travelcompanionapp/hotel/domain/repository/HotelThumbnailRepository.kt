package eu.ase.travelcompanionapp.hotel.domain.repository

import coil3.Bitmap
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

interface HotelThumbnailRepository {
    suspend fun getHotelThumbnail(hotel: Hotel): Bitmap?
}