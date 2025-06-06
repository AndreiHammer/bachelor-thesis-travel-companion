package eu.ase.travelcompanionapp.recommendation.data.destinationapi

import eu.ase.travelcompanionapp.hotel.data.amadeusApi.GeoCodeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("user_id") val userId: String? = null,
    @SerialName("preferences") val preferences: QuestionnaireResponseDto,
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
    @SerialName("budget_range") val budgetRange: String,
    @SerialName("travel_purpose") val travelPurpose: String,
    @SerialName("group_size") val groupSize: String,
    @SerialName("accommodation_type") val accommodationType: String,
    @SerialName("location_preference") val locationPreference: String,
    @SerialName("importance_factors") val importanceFactors: ImportanceFactorsDto,
    @SerialName("important_amenities") val importantAmenities: List<String>,
    @SerialName("preferred_continents") val preferredContinents: List<String>
)

@Serializable
data class ImportanceFactorsDto(
    @SerialName("amenities") val amenities: Int,
    @SerialName("hotel_rating") val hotelRating: Int,
    @SerialName("location") val location: Int,
    @SerialName("price") val price: Int
)

@Serializable
data class DestinationRecommendationResponse(
    @SerialName("user_id") val userId: String = "",
    @SerialName("destinations") val destinations: List<DestinationDto> = emptyList(),
    @SerialName("generated_at") val generatedAt: String? = null,
    @SerialName("reasoning") val reasoning: String = ""
)

@Serializable
data class DestinationDto(
    @SerialName("city") val city: String = "",
    @SerialName("country") val country: String = "",
    @SerialName("iata_code") val iataCode: String = "",
    @SerialName("continent") val continent: String = "",
    @SerialName("geo_code") val geoCode: GeoCodeDto = GeoCodeDto(),
    @SerialName("description") val description: String = "",
    @SerialName("match_reasons") val matchReasons: List<String> = emptyList(),
    @SerialName("best_for") val bestFor: List<String> = emptyList(),
    @SerialName("season_score") val seasonScore: Double = 0.0,
    @SerialName("budget_level") val budgetLevel: String = "",
    @SerialName("popular_attractions") val popularAttractions: List<String> = emptyList(),
)
