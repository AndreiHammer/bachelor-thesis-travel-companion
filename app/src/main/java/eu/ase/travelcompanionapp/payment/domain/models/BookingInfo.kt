package eu.ase.travelcompanionapp.payment.domain.models

data class BookingInfo(
    val bookingReference: String,
    val hotelId: String?,
    val hotelName: String?,
    val offerId: String?,
    val checkInDate: String?,
    val checkOutDate: String?,
    val roomType: String?,
    val guests: Int,
    val amount: Long,
    val currency: String,
    val timestamp: Long
)
