package eu.ase.travelcompanionapp.hotel.data.amadeusApi.network

import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.core.data.HttpClientFactory
import eu.ase.travelcompanionapp.core.data.safeCall
import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.core.domain.map
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.AmadeusOAuth2TokenResponse
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType

class AmadeusApiService(
    private val client: HttpClient
): RemoteHotelDataSource{

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
        } .map {
            it.accessToken
        }
    }

    override suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    ) {
        when (val tokenResult = getAccessToken()) {
            is Result.Error -> {
                onResult(Result.Error(tokenResult.error))
            }
            is Result.Success -> {
                val token = tokenResult.data
                safeCall<HotelSearchResponse> {
                    var url =
                        "https://test.api.amadeus.com/v1/reference-data/locations/hotels/by-city?cityCode=$city&radius=20&radiusUnit=KM"
                    if (amenities.isNotEmpty()) {
                        url = "$url&amenities=$amenities"
                    }
                    if (rating.isNotEmpty()) {
                        url = "$url&ratings=$rating"
                    }

                    client.get(url) {
                        headers {
                            bearerAuth(token)
                        }
                    }
                }.map { response ->
                    if (response.errors.isNotEmpty()) {
                        onResult(Result.Error(DataError.Remote.SERIALIZATION))
                    } else {
                        onResult(Result.Success(response.data.map { it }))
                    }
                }
            }
        }
    }

}