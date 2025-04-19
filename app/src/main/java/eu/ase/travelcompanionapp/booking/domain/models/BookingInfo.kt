package eu.ase.travelcompanionapp.booking.domain.models

data class BookingInfo(
    val bookingReference: String = "",
    val userId: String = "",
    val hotelId: String? = null,
    val hotelName: String? = null,
    val offerId: String? = null,
    val checkInDate: String? = null,
    val checkOutDate: String? = null,
    val roomType: String? = null,
    val guests: Int = 0,
    val amount: Long = 0,
    val currency: String = "",
    val paymentId: String? = null,
    val paymentStatus: String = "",
    val timestamp: Long = 0
)
