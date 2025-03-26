package eu.ase.travelcompanionapp.user.data.currencyApi.network

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.user.data.currencyApi.CurrencyDto

interface RemoteCurrencyDataSource {
    suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double,
        onResult: (Result<CurrencyDto, DataError.Remote>) -> Unit
    )
}