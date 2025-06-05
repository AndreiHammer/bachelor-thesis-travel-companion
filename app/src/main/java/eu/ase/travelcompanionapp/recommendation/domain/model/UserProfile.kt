package eu.ase.travelcompanionapp.recommendation.domain.model

import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

data class UserProfile(
    val userId: String,
    val preferences: QuestionnaireResponse,
    val visitedDestinations: List<String> = arrayListOf(),
    val savedHotels: ArrayList<Hotel> = arrayListOf(),
    val bookedOffers: ArrayList<BookingInfo> = arrayListOf(),
    val currentLocation: String?
)
