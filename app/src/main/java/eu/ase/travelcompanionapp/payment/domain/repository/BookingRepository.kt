package eu.ase.travelcompanionapp.payment.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.payment.domain.models.BookingInfo
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentResponse
import kotlinx.coroutines.flow.StateFlow

interface BookingService {
    val currentBooking: StateFlow<BookingInfo?>

    fun startBooking(hotelOffer: HotelOffer): BookingInfo
    suspend fun processPayment(bookingDetails: BookingInfo): Result<PaymentIntentResponse, DataError>
}