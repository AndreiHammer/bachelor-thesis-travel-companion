package eu.ase.travelcompanionapp.booking.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookingRecordRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) : BookingRecordRepository {

    private fun getUserBookingsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("bookings")

    override suspend fun saveBookingRecord(
        booking: BookingInfo, 
        paymentId: String
    ): Result<Unit, DataError> {
        return try {
            val userId = authRepository.currentUserId

            val bookingToSave = booking.copy(
                userId = userId,
                paymentId = paymentId,
                paymentStatus = "COMPLETED"
            )
            
            try {
                getUserBookingsCollection(userId).document(booking.bookingReference).set(bookingToSave).await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override suspend fun getBookingRecord(
        bookingReference: String
    ): Result<BookingInfo, DataError> {
        return try {
            val userId = authRepository.currentUserId
            val document = getUserBookingsCollection(userId).document(bookingReference).get().await()
            
            if (document.exists()) {
                val bookingRecord = document.toObject(BookingInfo::class.java)
                if (bookingRecord != null) {
                    Result.Success(bookingRecord)
                } else {
                    Result.Error(DataError.Remote.NOT_FOUND)
                }
            } else {
                Result.Error(DataError.Remote.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override fun getUserBookings(userId: String?): Flow<List<BookingInfo>> = callbackFlow {
        val currentUserId = userId ?: authRepository.currentUserId
        
        val listenerRegistration = getUserBookingsCollection(currentUserId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val bookings = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(BookingInfo::class.java)
                } ?: emptyList()

                trySend(bookings)
            }
            
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun deleteBookingRecord(
        bookingReference: String
    ): Result<Unit, DataError> {
        return try {
            val userId = authRepository.currentUserId
            getUserBookingsCollection(userId).document(bookingReference).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }
    
    override suspend fun sendBookingConfirmationEmail(
        booking: BookingInfo, 
        userEmail: String
    ): Result<Unit, DataError> {
        return try {
            val data = hashMapOf(
                "type" to "BOOKING_CONFIRMATION",
                "email" to userEmail,
                "bookingReference" to booking.bookingReference,
                "hotelName" to (booking.hotelName ?: ""),
                "checkInDate" to (booking.checkInDate ?: ""),
                "checkOutDate" to (booking.checkOutDate ?: ""),
                "roomType" to (booking.roomType ?: ""),
                "guests" to booking.guests.toString(),
                "amount" to (booking.amount / 100.0).toString(),
                "currency" to booking.currency
            )
            
            functions
                .getHttpsCallable("sendEmail")
                .call(data)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }
} 