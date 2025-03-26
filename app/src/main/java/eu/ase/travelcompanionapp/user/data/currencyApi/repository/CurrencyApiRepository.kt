package eu.ase.travelcompanionapp.user.data.currencyApi.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.user.data.currencyApi.network.RemoteCurrencyDataSource
import eu.ase.travelcompanionapp.user.data.currencyApi.toCurrency
import eu.ase.travelcompanionapp.user.domain.model.Currency
import eu.ase.travelcompanionapp.user.domain.repository.CurrencyRepository

class CurrencyApiRepository(
    private val remoteCurrencyDataSource: RemoteCurrencyDataSource
): CurrencyRepository {
    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double,
        onResult: (Result<Currency, DataError.Remote>) -> Unit
    ) {
        remoteCurrencyDataSource.convertCurrency(from, to, amount) { result ->
            when (result) {
                is Result.Error -> onResult(Result.Error(result.error))
                is Result.Success -> {
                    onResult(Result.Success(result.data.toCurrency()))
                }
            }

        }
    }
}