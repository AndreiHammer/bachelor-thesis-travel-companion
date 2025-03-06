package eu.ase.travelcompanionapp.hotel.data.amadeusApi.network

import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.core.data.network.safeCall
import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.core.domain.map
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.AmadeusOAuth2TokenResponse
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.Errors
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class HotelApiHelper(private val client: HttpClient) {

    suspend fun getAccessToken(): Result<String, DataError.Remote> {
        val formData = Parameters.build {
            append("grant_type", "client_credentials")
            append("client_id", BuildConfig.AMADEUS_API_KEY)
            append("client_secret", BuildConfig.AMADEUS_API_SECRET)
        }

        return safeCall<AmadeusOAuth2TokenResponse> {
            client.post("https://test.api.amadeus.com/v1/security/oauth2/token") {
                setBody(FormDataContent(formData))
                contentType(ContentType.Application.FormUrlEncoded)
            }
        }.map {
            it.accessToken
        }
    }

    private fun handleApiError(error: Errors): Result<List<HotelDto>, DataError.Remote> {
        return when {
            error.status == 400 && error.code == 895 -> Result.Success(emptyList())
            else -> Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    private suspend fun fetchHotelsWithRating(
        baseUrl: String, 
        rating: String, 
        token: String
    ): Result<HotelSearchResponse, DataError.Remote> {
        return safeCall {
            client.get("$baseUrl&ratings=$rating") {
                headers {
                    bearerAuth(token)
                }
            }
        }
    }

    private suspend fun fetchAllHotels(
        baseUrl: String,
        token: String
    ): Result<HotelSearchResponse, DataError.Remote> {
        return safeCall {
            client.get(baseUrl) {
                headers {
                    bearerAuth(token)
                }
            }
        }
    }

    suspend fun handleHotelSearch(
        baseUrl: String,
        rating: String,
        token: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    ) = coroutineScope {
        if (rating.isEmpty() || rating == "1,2,3,4,5") {
            val baseResponse = fetchAllHotels(baseUrl, token)
            
            if (baseResponse is Result.Error) {
                onResult(Result.Error(baseResponse.error))
                return@coroutineScope
            }
            
            val baseHotels = (baseResponse as Result.Success).data
            
            if (baseHotels.errors.isNotEmpty()) {
                onResult(handleApiError(baseHotels.errors.first()))
                return@coroutineScope
            }

            val ratingValues = listOf("1", "2", "3", "4", "5")
            val ratedResponses = mutableListOf<HotelSearchResponse>()

            val deferredResults = ratingValues.map { ratingValue ->
                async { fetchHotelsWithRating(baseUrl, ratingValue, token) }
            }
            deferredResults.forEach { deferred ->
                val result = deferred.await()
                if (result is Result.Success && result.data.errors.isEmpty()) {
                    ratedResponses.add(result.data)
                }
            }

            val ratedHotelsMap = mutableMapOf<String?, HotelDto>()

            ratedResponses.forEach { response ->
                response.data.forEach { hotel ->
                    if (hotel.rating != null && hotel.hotelId != null) {
                        ratedHotelsMap[hotel.hotelId] = hotel
                    }
                }
            }

            val mergedHotels = baseHotels.data.map { hotel ->
                ratedHotelsMap[hotel.hotelId] ?: hotel
            }.toMutableList()

            ratedHotelsMap.values.forEach { ratedHotel ->
                if (mergedHotels.none { it.hotelId == ratedHotel.hotelId }) {
                    mergedHotels.add(ratedHotel)
                }
            }
            
            onResult(Result.Success(mergedHotels))
        } else {
            val selectedRatings = rating.split(",")

            if (selectedRatings.size == 1) {
                when (val response = fetchHotelsWithRating(baseUrl, rating, token)) {
                    is Result.Error -> onResult(Result.Error(response.error))
                    is Result.Success -> {
                        if (response.data.errors.isNotEmpty()) {
                            onResult(handleApiError(response.data.errors.first()))
                        } else {
                            onResult(Result.Success(response.data.data))
                        }
                    }
                }
            } else {
                val allHotels = mutableListOf<HotelDto>()

                val deferredResults = selectedRatings.map { selectedRating ->
                    async { fetchHotelsWithRating(baseUrl, selectedRating, token) }
                }
                deferredResults.forEach { deferred ->
                    val result = deferred.await()
                    if (result is Result.Success && result.data.errors.isEmpty()) {
                        allHotels.addAll(result.data.data)
                    }
                }
                
                onResult(Result.Success(allHotels))
            }
        }
    }
} 