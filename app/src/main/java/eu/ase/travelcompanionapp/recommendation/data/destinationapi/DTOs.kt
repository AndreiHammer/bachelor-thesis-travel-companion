package eu.ase.travelcompanionapp.recommendation.data.destinationapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("user_id") val userId: String? = null,
    @SerialName("preferences") val preferences: QuestionnaireResponseDto = QuestionnaireResponseDto(),
    @SerialName("visited_destinations") val visitedDestinations: List<String> = emptyList(),
    @SerialName("saved_hotels") val savedHotels: List<SavedHotelDto> = emptyList(),
    @SerialName("booked_offers") val bookedOffers: List<BookedOfferDto> = emptyList(),
    @SerialName("current_location") val currentLocation: String? = null
)

@Serializable
data class SavedHotelDto(
    @SerialName("amenities") val amenities: List<String> = emptyList(),
    @SerialName("chainCode") val chainCode: String = "",
    @SerialName("countryCode") val countryCode: String = "",
    @SerialName("dupeId") val dupeId: Int = 0,
    @SerialName("giataId") val giataId: Int? = null,
    @SerialName("hotelId") val hotelId: String = "",
    @SerialName("iataCode") val iataCode: String = "",
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("name") val name: String = "",
    @SerialName("phone") val phone: String? = null,
    @SerialName("rating") val rating: Int = 0
)

@Serializable
data class BookedOfferDto(
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("bookingReference") val bookingReference: String = "",
    @SerialName("checkInDate") val checkInDate: String = "",
    @SerialName("checkOutDate") val checkOutDate: String = "",
    @SerialName("currency") val currency: String = "",
    @SerialName("guests") val guests: Int = 0,
    @SerialName("hotelId") val hotelId: String = "",
    @SerialName("hotelName") val hotelName: String = "",
    @SerialName("offerId") val offerId: String = "",
    @SerialName("paymentId") val paymentId: String = "",
    @SerialName("paymentStatus") val paymentStatus: String = "",
    @SerialName("roomType") val roomType: String = "",
    @SerialName("timestamp") val timestamp: Int = 0
)

@Serializable
data class QuestionnaireResponseDto(
    @SerialName("budget_range") val budgetRange: String = "Mid-range",
    @SerialName("travel_purpose") val travelPurpose: String = "Leisure",
    @SerialName("group_size") val groupSize: String = "Solo",
    @SerialName("accommodation_type") val accommodationType: String = "Any type",
    @SerialName("location_preference") val locationPreference: String = "City center",
    @SerialName("importance_factors") val importanceFactors: ImportanceFactorsDto = ImportanceFactorsDto(),
    @SerialName("important_amenities") val importantAmenities: List<String> = emptyList(),
    @SerialName("preferred_continents") val preferredContinents: List<String> = emptyList()
)

@Serializable
data class ImportanceFactorsDto(
    @SerialName("amenities") val amenities: Int = 5,
    @SerialName("hotel_rating") val hotelRating: Int = 5,
    @SerialName("location") val location: Int = 5,
    @SerialName("price") val price: Int = 5
)

