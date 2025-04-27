package eu.ase.travelcompanionapp.hotel.data.recommendation

import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.RecommendationRepository
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

class RecommendationRepositoryImpl(
    private val favouriteHotelRepository: FavouriteHotelRepository
) : RecommendationRepository {
    override fun getPersonalizedRecommendations(limit: Int): Flow<List<Hotel>> {
        return favouriteHotelRepository.getFavouriteHotels()
    }

    override suspend fun getSimilarHotels(hotelId: String, limit: Int): List<Hotel> {
        return emptyList()
    }

    override suspend fun getRecommendationExplanation(hotel: Hotel): String {
        return ""
    }
} 