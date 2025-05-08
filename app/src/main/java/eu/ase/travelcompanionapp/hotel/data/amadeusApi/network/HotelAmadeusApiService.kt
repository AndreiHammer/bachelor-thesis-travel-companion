package eu.ase.travelcompanionapp.hotel.data.amadeusApi.network

import eu.ase.travelcompanionapp.core.data.network.safeCall
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.map
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelOffersDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelOffersResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.headers

class HotelAmadeusApiService(
    private val client: HttpClient
): RemoteHotelDataSource {

    private val hotelApiHelper = HotelApiHelper(client)

    override suspend fun searchHotelsByCity(
        city: String,
        amenities: String,
        rating: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    ) {
        when (val tokenResult = hotelApiHelper.getAccessToken()) {
            is Result.Error -> onResult(Result.Error(tokenResult.error))
            is Result.Success -> {
                val baseUrl = "https://test.api.amadeus.com/v1/reference-data/locations/hotels/by-city?cityCode=$city&radius=20&radiusUnit=KM" + 
                    (if (amenities.isNotEmpty()) "&amenities=$amenities" else "")
                
                hotelApiHelper.handleHotelSearch(baseUrl, rating, tokenResult.data, onResult)
            }
        }
    }

    override suspend fun searchHotelsByLocation(
        latitude: Double,
        longitude: Double,
        radius: Int,
        amenities: String,
        rating: String,
        onResult: (Result<List<HotelDto>, DataError.Remote>) -> Unit
    ) {
        when (val tokenResult = hotelApiHelper.getAccessToken()) {
            is Result.Error -> onResult(Result.Error(tokenResult.error))
            is Result.Success -> {
                val baseUrl = "https://test.api.amadeus.com/v1/reference-data/locations/hotels/by-geocode?latitude=$latitude&longitude=$longitude&radius=$radius&radiusUnit=KM" +
                    (if (amenities.isNotEmpty()) "&amenities=$amenities" else "")
                
                hotelApiHelper.handleHotelSearch(baseUrl, rating, tokenResult.data, onResult)
            }
        }
    }

    override suspend fun searchHotelOffers(
        hotelIds: String,
        checkInDate: String,
        checkOutDate: String,
        adults: String,
        bestRateOnly: Boolean,
        onResult: (Result<List<HotelOffersDto>, DataError.Remote>) -> Unit
    ) {
        when (val tokenResult = hotelApiHelper.getAccessToken()) {
            is Result.Error -> {
                onResult(Result.Error(tokenResult.error))
            }
            is Result.Success -> {
                val token = tokenResult.data
                safeCall<HotelOffersResponse> {
                    val url = "https://test.api.amadeus.com/v3/shopping/hotel-offers?hotelIds=$hotelIds&adults=$adults&includeClosed=false&bestRateOnly=$bestRateOnly&checkInDate=$checkInDate&checkOutDate=$checkOutDate"
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