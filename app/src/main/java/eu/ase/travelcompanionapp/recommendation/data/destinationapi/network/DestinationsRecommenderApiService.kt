package eu.ase.travelcompanionapp.recommendation.data.destinationapi.network

import eu.ase.travelcompanionapp.core.data.network.safeCall
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.map
import eu.ase.travelcompanionapp.recommendation.data.destinationapi.DestinationRecommendationResponse
import eu.ase.travelcompanionapp.recommendation.data.destinationapi.toDestinationRecommendation
import eu.ase.travelcompanionapp.recommendation.data.destinationapi.toUserProfileDto
import eu.ase.travelcompanionapp.recommendation.domain.model.RecommendedDestinations
import eu.ase.travelcompanionapp.recommendation.domain.model.UserProfile
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationApiRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


const val BASE_URL = "http://10.0.2.2:8000"
class DestinationsRecommenderApiService(
    private val client: HttpClient
) : DestinationApiRepository {
    
    override suspend fun createUserProfile(userProfile: UserProfile): Result<Unit, DataError> {
        val dto = userProfile.toUserProfileDto()
        val url = "$BASE_URL/users/profile"

        return safeCall<Unit> {
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
        }
    }

    override suspend fun getRecommendedDestinations(userId: String): Result<RecommendedDestinations, DataError> {
        return safeCall<DestinationRecommendationResponse> {
            val url = "$BASE_URL/users/$userId/recommendations/destinations"
            client.get(url)
        }.map { response ->
            response.toDestinationRecommendation()
        }
    }
}