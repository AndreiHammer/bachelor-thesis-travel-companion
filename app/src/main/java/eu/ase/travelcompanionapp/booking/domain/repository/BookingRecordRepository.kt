package eu.ase.travelcompanionapp.booking.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import kotlinx.coroutines.flow.Flow

interface BookingRecordRepository {
    suspend fun saveBookingRecord(booking: BookingInfo, paymentId: String): Result<Unit, DataError>
    
    suspend fun getBookingRecord(bookingReference: String): Result<BookingInfo, DataError>
    
    fun getUserBookings(userId: String? = null): Flow<List<BookingInfo>>
    
    suspend fun deleteBookingRecord(bookingReference: String): Result<Unit, DataError>
    
    suspend fun sendBookingConfirmationEmail(booking: BookingInfo, userEmail: String): Result<Unit, DataError>
} 