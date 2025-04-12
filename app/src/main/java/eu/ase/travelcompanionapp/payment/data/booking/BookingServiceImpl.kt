package eu.ase.travelcompanionapp.payment.data.booking

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.payment.domain.models.BookingInfo
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentResponse
import eu.ase.travelcompanionapp.payment.domain.repository.BookingService
import eu.ase.travelcompanionapp.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BookingServiceImpl(
    private val paymentRepository: PaymentRepository
) : BookingService {
    private val _currentBooking = MutableStateFlow<BookingInfo?>(null)
    override val currentBooking: StateFlow<BookingInfo?> = _currentBooking.asStateFlow()

    override fun startBooking(hotelOffer: HotelOffer): BookingInfo {
        val bookingReference = generateBookingReference()
        val selectedOffer = hotelOffer.offers.firstOrNull()

        val hotelName = hotelOffer.hotel?.name ?: "Unknown Hotel"
        val roomType = selectedOffer?.room?.description ?: 
                      (selectedOffer?.room?.typeEstimated?.category ?: "Standard Room")
        val checkInDate = selectedOffer?.checkInDate ?: "Not specified"
        val checkOutDate = selectedOffer?.checkOutDate ?: "Not specified"
        val guests = selectedOffer?.guests?.adults ?: 1

        val amountRaw = selectedOffer?.price?.total?.toDoubleOrNull() ?: 0.0
        val amount = (amountRaw * 100).toLong()
        val currency = selectedOffer?.price?.currency ?: "EUR"

        val bookingDetails = BookingInfo(
            bookingReference = bookingReference,
            hotelId = hotelOffer.hotel?.hotelId,
            hotelName = hotelName,
            offerId = selectedOffer?.id,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            roomType = roomType,
            guests = guests,
            amount = amount,
            currency = currency,
            timestamp = System.currentTimeMillis()
        )

        _currentBooking.value = bookingDetails
        return bookingDetails
    }

    override suspend fun processPayment(bookingDetails: BookingInfo): Result<PaymentIntentResponse, DataError> {
        val metadata = mapOf(
            "booking_reference" to bookingDetails.bookingReference,
            "hotel_id" to (bookingDetails.hotelId ?: ""),
            "hotel_name" to (bookingDetails.hotelName ?: ""),
            "check_in" to (bookingDetails.checkInDate ?: ""),
            "check_out" to (bookingDetails.checkOutDate ?: ""),
            "guests" to bookingDetails.guests.toString(),
            "room_type" to (bookingDetails.roomType ?: "")
        )

        return paymentRepository.createPaymentIntent(
            amount = bookingDetails.amount,
            currency = bookingDetails.currency,
            description = "Booking ${bookingDetails.bookingReference} at ${bookingDetails.hotelName}",
            metadata = metadata
        )
    }

    private fun generateBookingReference() = "TC-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(6)}"
}