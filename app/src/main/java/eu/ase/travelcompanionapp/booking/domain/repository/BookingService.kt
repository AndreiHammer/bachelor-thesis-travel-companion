package eu.ase.travelcompanionapp.booking.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.booking.domain.models.PaymentIntentResponse
import kotlinx.coroutines.flow.StateFlow

interface BookingService {
    val currentBooking: StateFlow<BookingInfo?>

    fun startBooking(hotelOffer: HotelOffer): BookingInfo
    
    fun startBookingWithConvertedPrice(
        hotelOffer: HotelOffer,
        convertedAmount: Double,
        convertedCurrency: String
    ): BookingInfo
    
    suspend fun processPayment(bookingDetails: BookingInfo): Result<PaymentIntentResponse, DataError>
} 