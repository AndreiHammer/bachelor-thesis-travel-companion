package eu.ase.travelcompanionapp.recommendation.data.destinationapi

import eu.ase.travelcompanionapp.app.navigation.routes.DestinationRoute
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination

import eu.ase.travelcompanionapp.recommendation.domain.model.QuestionnaireResponse
import eu.ase.travelcompanionapp.recommendation.domain.model.RecommendedDestinations
import eu.ase.travelcompanionapp.recommendation.domain.model.UserProfile


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


fun BookingInfo.toBookedOfferDto(): BookedOfferDto {
    return BookedOfferDto(
        amount = this.amount.toDouble() / 100.0,
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
        timestamp = (this.timestamp / 1000).toInt()
    )
}


fun QuestionnaireResponse.toQuestionnaireResponseDto(): QuestionnaireResponseDto {
    return QuestionnaireResponseDto(
        preferredActivities = this.preferredActivities,
        climatePreference = this.climatePreference.takeIf { it.isNotBlank() } ?: "",
        travelStyle = this.travelStyle.takeIf { it.isNotBlank() } ?: "",
        tripDuration = this.tripDuration.takeIf { it.isNotBlank() } ?: "",
        companions = this.companions.takeIf { it.isNotBlank() } ?: "",
        culturalOpenness = this.culturalOpenness,
        preferredCountry = this.preferredCountry,
        bucketListThemes = this.bucketListThemes,
        budgetRange = this.budgetRange.takeIf { it.isNotBlank() } ?: "",
        preferredContinents = this.preferredContinents
    )
}

fun DestinationRecommendationResponse.toDestinationRecommendation(): RecommendedDestinations {
    return RecommendedDestinations(
        userId = this.userId,
        destinations = ArrayList(this.destinations.map { it.toDestination() }),
        generatedAt = this.generatedAt,
        reasoning = this.reasoning
    )
}

fun DestinationDto.toDestination(): Destination {
    return Destination(
        city = this.city,
        country = this.country,
        iataCode = this.iataCode,
        continent = this.continent,
        latitude = this.geoCode.latitude ?: 0.0,
        longitude = this.geoCode.longitude ?: 0.0,
        description = this.description,
        matchReasons = this.matchReasons,
        bestFor = this.bestFor,
        seasonScore = this.seasonScore.toInt(),
        budgetLevel = this.budgetLevel,
        popularAttractions = this.popularAttractions
    )
}

fun DestinationRoute.DestinationDetail.toDestination() : Destination {
    return Destination(
        city = this.city,
        country = this.country,
        iataCode = this.iataCode,
        continent = this.continent,
        latitude = this.latitude,
        longitude = this.longitude,
        description = this.description,
        matchReasons = this.matchReasons,
        bestFor = this.bestFor,
        seasonScore = this.seasonScore,
        budgetLevel = this.budgetLevel,
        popularAttractions = this.popularAttractions
    )
}
