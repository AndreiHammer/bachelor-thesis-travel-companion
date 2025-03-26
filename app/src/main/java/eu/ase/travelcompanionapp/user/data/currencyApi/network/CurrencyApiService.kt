package eu.ase.travelcompanionapp.user.data.currencyApi.network

import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.core.data.network.safeCall
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.map
import eu.ase.travelcompanionapp.user.data.currencyApi.CurrencyDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers

class CurrencyApiService(
    private val client: HttpClient
): RemoteCurrencyDataSource {

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double,
        onResult: (Result<CurrencyDto, DataError.Remote>) -> Unit
    ) {
        safeCall<CurrencyDto> {
            client.get("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=$from&to=$to&amount=$amount") {
                headers {
                    append("x-rapidapi-key", BuildConfig.RAPID_API_KEY)
                    append("x-rapidapi-host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
                }
            }
        }.map { response->
            onResult(Result.Success(response))
        }
    }

}