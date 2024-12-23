package eu.ase.travelcompanionapp.hotel.data.mappers

import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelDto
import eu.ase.travelcompanionapp.hotel.domain.Hotel

fun HotelDto.toHotel(): Hotel {
    return Hotel(
        hotelId = this.hotelId ?: "",
        chainCode = this.chainCode,
        iataCode = this.iataCode ?: "",
        dupeId = this.dupeId,
        name = this.name ?: "",
        latitude = this.geoCode?.latitude ?: 0.0,
        longitude = this.geoCode?.longitude ?: 0.0,
        countryCode = this.address?.countryCode ?: "",
        amenities = this.amenities,
        rating = this.rating,
        giataId = this.giataId,
        phone = this.contact?.phone
    )
}