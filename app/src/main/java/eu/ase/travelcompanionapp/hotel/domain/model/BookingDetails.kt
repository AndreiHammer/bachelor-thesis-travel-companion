package eu.ase.travelcompanionapp.hotel.domain.model

data class BookingDetails(
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val adults: Int = 0
)
