package eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.network

import eu.ase.travelcompanionapp.core.data.network.safeCall
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.map
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.HotelApiHelper
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.TouristAttractionDto
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.TouristAttractionResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.headers

class TouristAttractionsAmadeusApiService(
    private val client: HttpClient
) : RemoteTouristAttractionsDataSource {

    private val hotelApiHelper = HotelApiHelper(client)

    override suspend fun searchTouristAttractionsByLocation(
        latitude: Double,
        longitude: Double,
        onResult: (Result<List<TouristAttractionDto>, DataError.Remote>) -> Unit
    ) {
        when (val tokenResult = hotelApiHelper.getAccessToken()) {
            is Result.Error -> {
                onResult(Result.Error(tokenResult.error))
            }
            is Result.Success -> {
                val token = tokenResult.data
                safeCall<TouristAttractionResponse> {
                    val url = "https://test.api.amadeus.com/v1/shopping/activities?latitude=$latitude&longitude=$longitude&radius=2"
                    client.get(url) {
                        headers {
                            bearerAuth(token)
                        }
                    }
                }.map { response ->
                    onResult(Result.Success(response.data.map { it }))
                }
            }
        }
    }
}

