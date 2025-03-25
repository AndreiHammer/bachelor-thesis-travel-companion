package eu.ase.travelcompanionapp.hotel.data.database

import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.hotel.data.database.repository.RemoteFavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.model.BookingDetails
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteHotelRepositoryImpl(
    private val remoteRepository: RemoteFavouriteHotelRepository,
    private val accountRepository: AuthRepository
    ) : FavouriteHotelRepository {

    override fun getFavouriteHotels(): Flow<List<Hotel>> = flow {
        if (!accountRepository.hasUser()) {
            emit(emptyList())
            return@flow
        }

        val userId = accountRepository.currentUserId
        try {
            val remoteHotels = remoteRepository.getFavouriteHotels(userId)
            emit(remoteHotels)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun isFavourite(hotelId: String): Boolean {
        if (!accountRepository.hasUser()) return false

        val userId = accountRepository.currentUserId
        return try {
            remoteRepository.isFavourite(userId, hotelId)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addFavourite(
        hotel: Hotel,
        checkInDate: String?,
        checkOutDate: String?,
        adults: Int?
    ) {
        if (!accountRepository.hasUser()) return

        val userId = accountRepository.currentUserId
        try {
            remoteRepository.addFavourite(userId, hotel, checkInDate, checkOutDate, adults)
        } catch (e: Exception) {
            // Handle error
        }
    }

    override suspend fun removeFavourite(hotelId: String) {
        if (!accountRepository.hasUser()) return

        val userId = accountRepository.currentUserId
        try {
            remoteRepository.removeFavourite(userId, hotelId)
        } catch (e: Exception) {
            // Handle error
        }
    }

    override suspend fun getBookingDetails(hotelId: String): BookingDetails? {
        if (!accountRepository.hasUser()) return null

        val userId = accountRepository.currentUserId
        try {

            val document = remoteRepository.getHotelDocument(userId, hotelId)

            val checkInDate = document?.getString("checkInDate")
            val checkOutDate = document?.getString("checkOutDate")
            val adults = document?.getLong("adults")?.toInt()

            return if (checkInDate != null && checkOutDate != null && adults != null) {
                BookingDetails(
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = adults
                )
            } else null
        } catch (e: Exception) {
            return null
        }
    }

}