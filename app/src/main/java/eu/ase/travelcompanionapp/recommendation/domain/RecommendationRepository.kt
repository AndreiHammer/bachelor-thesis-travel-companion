package eu.ase.travelcompanionapp.recommendation.domain

import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.recommendation.domain.model.CityDestination
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getPersonalizedRecommendations(limit: Int = 10): Flow<List<Hotel>>

    suspend fun getSimilarHotels(hotelId: String, limit: Int = 5): List<Hotel>

    suspend fun getRecommendationExplanation(hotel: Hotel): String
    
    fun getDestinationRecommendations(limit: Int = 10): Flow<List<CityDestination>>
    
    suspend fun getDestinationExplanation(destination: CityDestination): String
} 