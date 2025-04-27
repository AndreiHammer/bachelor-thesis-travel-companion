package eu.ase.travelcompanionapp.hotel.domain.repository

import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getPersonalizedRecommendations(limit: Int = 10): Flow<List<Hotel>>

    suspend fun getSimilarHotels(hotelId: String, limit: Int = 5): List<Hotel>

    suspend fun getRecommendationExplanation(hotel: Hotel): String
} 