package eu.ase.travelcompanionapp.user.domain.service

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.user.domain.model.Currency
import eu.ase.travelcompanionapp.user.domain.repository.CurrencyRepository
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull

class PriceConverter(
    private val currencyRepository: CurrencyRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun convertPrice(
        price: Double,
        fromCurrency: String,
        onResult: (Result<Currency, DataError.Remote>) -> Unit
    ) {
        val preferredCurrency = userPreferencesRepository.preferredCurrency.firstOrNull()

        if (preferredCurrency.isNullOrEmpty() || preferredCurrency == fromCurrency) {
            val originalCurrency = Currency(
                code = fromCurrency,
                name = Currency.getCurrencyName(fromCurrency),
                rate = 1.0,
                convertedAmount = price,
                originalAmount = price,
                originalCurrency = fromCurrency
            )
            onResult(Result.Success(originalCurrency))
            return
        }

        currencyRepository.convertCurrency(
            from = fromCurrency,
            to = preferredCurrency,
            amount = price,
            onResult = onResult
        )
    }
}