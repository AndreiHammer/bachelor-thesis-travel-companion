package eu.ase.travelcompanionapp.user.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.user.domain.model.Currency

interface CurrencyRepository {
    suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double,
        onResult: (Result<Currency, DataError.Remote>) -> Unit
    )
}