package eu.ase.travelcompanionapp.hotel.domain.model

data class HotelWithBookingDetails (
    val hotel: Hotel,
    val bookingDetails: BookingDetails? = null
)