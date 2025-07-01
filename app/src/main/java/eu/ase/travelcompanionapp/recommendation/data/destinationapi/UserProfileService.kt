package eu.ase.travelcompanionapp.recommendation.data.destinationapi

import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.recommendation.domain.model.QuestionnaireResponse
import eu.ase.travelcompanionapp.recommendation.domain.model.UserProfile
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationApiRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class UserProfileService(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val favouriteHotelRepository: FavouriteHotelRepository,
    private val bookingRecordRepository: BookingRecordRepository,
    private val destinationApiRepository: DestinationApiRepository,
    private val authRepository: AuthRepository
) : UserProfileRepository {

    override suspend fun createAndSendUserProfile(
        currentLocation: String?
    ): Result<Unit, DataError> {
        val validationResult = validateUserProfileData()
        if (validationResult is Result.Error) {
            return validationResult
        }
        return try {
            val userProfile = buildUserProfile()
            when (val result = destinationApiRepository.createUserProfile(userProfile)) {
                is Result.Success -> {
                    Result.Success(Unit)
                }
                is Result.Error -> {
                    result
                }
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun getUserProfilePreview(): UserProfile {
        return buildUserProfile()
    }
    
    override suspend fun canCreateUserProfile(): Boolean {
        return userPreferencesRepository.hasQuestionnaire()
    }
    
    override suspend fun validateUserProfileData(): Result<Unit, DataError> {
        return try {
            if (!canCreateUserProfile()) {
                return Result.Error(DataError.Local.QUESTIONNAIRE_NOT_COMPLETED)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.INSUFFICIENT_DATA)
        }
    }

    private suspend fun buildUserProfile(): UserProfile {
        val userId = authRepository.currentUserId

        val questionnaireResponse = userPreferencesRepository.questionnaireResponse.firstOrNull()
            ?: getDefaultQuestionnaireResponse()

        val allFavouriteHotels = favouriteHotelRepository.getFavouriteHotels().first()

        val favouriteHotels = allFavouriteHotels.filter { hotel ->
            hotel.hotelId.isNotEmpty() && hotel.name.isNotEmpty()
        }

        val bucharestLocation = "Bucharest, Romania (44.4268, 26.1025)"
        val bookingRecords = bookingRecordRepository.getUserBookings(userId.ifEmpty { null }).first()
        val visitedDestinations = bookingRecords
            .mapNotNull { booking ->
                extractDestinationFromBooking(booking.hotelName)
            }
            .distinct()
            .let { ArrayList(it) }
        
        val profile = UserProfile(
            userId = userId,
            preferences = questionnaireResponse,
            visitedDestinations = visitedDestinations,
            savedHotels = ArrayList(favouriteHotels),
            bookedOffers = ArrayList(bookingRecords),
            currentLocation = bucharestLocation,
        )
        return profile
    }

    private fun getDefaultQuestionnaireResponse(): QuestionnaireResponse {
        return QuestionnaireResponse(
            preferredActivities = arrayListOf(),
            climatePreference = "",
            travelStyle = "",
            tripDuration = "",
            companions = "",
            culturalOpenness = 5,
            preferredCountry = "",
            bucketListThemes = arrayListOf(),
            budgetRange = "",
            preferredContinents = arrayListOf()
        )
    }

    private fun extractDestinationFromBooking(hotelName: String?): String? {
        hotelName ?: return null
        val cityPatterns = listOf(
            "London", "Paris", "New York", "Tokyo", "Rome", "Barcelona", "Amsterdam", 
            "Berlin", "Vienna", "Prague", "Budapest", "Madrid", "Lisbon", "Athens",
            "Dublin", "Edinburgh", "Stockholm", "Copenhagen", "Oslo", "Helsinki",
            "Warsaw", "Krakow", "Bucharest", "Sofia", "Zagreb", "Ljubljana",
            "Bratislava", "Tallinn", "Riga", "Vilnius", "Malta", "Cyprus"
        )
        
        for (city in cityPatterns) {
            if (hotelName.contains(city, ignoreCase = true)) {
                return city
            }
        }
        return null
    }
} 