package eu.ase.travelcompanionapp.recommendation.data.destinationapi

import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.recommendation.domain.model.ImportanceFactors
import eu.ase.travelcompanionapp.recommendation.domain.model.QuestionnaireResponse
import eu.ase.travelcompanionapp.recommendation.domain.model.UserProfile

fun UserProfileDto.toUserProfile(): UserProfile {
    return UserProfile(
        userId = this.userId ?: "",
        preferences = this.preferences.toQuestionnaireResponse(),
        visitedDestinations = this.visitedDestinations,
        savedHotels = this.savedHotels.map { it.toHotel() } as ArrayList<Hotel>,
        bookedOffers = this.bookedOffers.map { it.toBookingInfo() } as ArrayList<BookingInfo>,
        currentLocation = this.currentLocation
    )
}

fun UserProfile.toUserProfileDto(): UserProfileDto {
    return UserProfileDto(
        userId = this.userId,
        preferences = this.preferences.toQuestionnaireResponseDto(),
        visitedDestinations = this.visitedDestinations,
        savedHotels = this.savedHotels.map { hotel -> hotel.toSavedHotelDto() },
        bookedOffers = this.bookedOffers.map { it.toBookedOfferDto() },
        currentLocation = this.currentLocation
    )
}


fun Hotel.toSavedHotelDto(): SavedHotelDto {
    return SavedHotelDto(
        amenities = this.amenities,
        chainCode = this.chainCode ?: "",
        countryCode = this.countryCode,
        dupeId = this.dupeId ?: 0,
        giataId = this.giataId,
        hotelId = this.hotelId,
        iataCode = this.iataCode,
        latitude = this.latitude,
        longitude = this.longitude,
        name = this.name,
        phone = this.phone,
        rating = this.rating ?: 0
    )
}

fun SavedHotelDto.toHotel(): Hotel {
    return Hotel(
        amenities = ArrayList(this.amenities),
        chainCode = this.chainCode,
        countryCode = this.countryCode,
        dupeId = this.dupeId,
        giataId = this.giataId,
        hotelId = this.hotelId,
        iataCode = this.iataCode,
        latitude = this.latitude,
        longitude = this.longitude,
        name = this.name,
        phone = this.phone,
        rating = this.rating
    )
}

fun BookingInfo.toBookedOfferDto(): BookedOfferDto {
    return BookedOfferDto(
        amount = this.amount.toDouble() / 100.0, // Convert from cents to currency
        bookingReference = this.bookingReference,
        checkInDate = this.checkInDate ?: "",
        checkOutDate = this.checkOutDate ?: "",
        currency = this.currency,
        guests = this.guests,
        hotelId = this.hotelId ?: "",
        hotelName = this.hotelName ?: "",
        offerId = this.offerId ?: "",
        paymentId = this.paymentId ?: "",
        paymentStatus = this.paymentStatus,
        roomType = this.roomType ?: "",
        timestamp = (this.timestamp / 1000).toInt() // Convert to smaller timestamp (seconds instead of milliseconds)
    )
}

fun BookedOfferDto.toBookingInfo(): BookingInfo {
    return BookingInfo(
        amount = (this.amount * 100).toLong(), // Convert back to cents
        bookingReference = this.bookingReference,
        checkInDate = this.checkInDate,
        checkOutDate = this.checkOutDate,
        currency = this.currency,
        guests = this.guests,
        hotelId = this.hotelId,
        hotelName = this.hotelName,
        offerId = this.offerId,
        paymentId = this.paymentId,
        paymentStatus = this.paymentStatus,
        roomType = this.roomType,
        timestamp = this.timestamp.toLong() * 1000 // Convert back to milliseconds
    )
}

fun QuestionnaireResponseDto.toQuestionnaireResponse(): QuestionnaireResponse {
    return QuestionnaireResponse(
        budgetRange = this.budgetRange,
        travelPurpose = this.travelPurpose,
        groupSize = this.groupSize,
        accommodationType = this.accommodationType,
        locationPreference = this.locationPreference,
        importanceFactors = this.importanceFactors.toImportanceFactors(),
        importantAmenities = ArrayList(this.importantAmenities),
        preferredContinents = ArrayList(this.preferredContinents)
    )
}

fun QuestionnaireResponse.toQuestionnaireResponseDto(): QuestionnaireResponseDto {
    return QuestionnaireResponseDto(
        budgetRange = this.budgetRange.ifEmpty { "Mid-range" },
        travelPurpose = this.travelPurpose.ifEmpty { "Leisure" },
        groupSize = this.groupSize.ifEmpty { "Solo" },
        accommodationType = this.accommodationType.ifEmpty { "Any type" },
        locationPreference = this.locationPreference.ifEmpty { "City center" },
        importanceFactors = this.importanceFactors.toImportanceFactorsDto(),
        importantAmenities = this.importantAmenities,
        preferredContinents = this.preferredContinents
    )
}

fun ImportanceFactorsDto.toImportanceFactors(): ImportanceFactors {
    return ImportanceFactors(
        amenities = this.amenities,
        hotelRating = this.hotelRating,
        location = this.location,
        price = this.price
    )
}

fun ImportanceFactors.toImportanceFactorsDto(): ImportanceFactorsDto {
    return ImportanceFactorsDto(
        amenities = this.amenities,
        hotelRating = this.hotelRating,
        location = this.location,
        price = this.price
    )
}
