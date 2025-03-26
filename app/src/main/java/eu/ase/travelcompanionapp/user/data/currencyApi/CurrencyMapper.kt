package eu.ase.travelcompanionapp.user.data.currencyApi

import eu.ase.travelcompanionapp.user.domain.model.Currency

fun CurrencyDto.toCurrency(): Currency {
    return Currency(
        code = this.query.to,
        name = Currency.getCurrencyName(this.query.to),
        rate = this.info.rate,
        convertedAmount = this.result,
        originalAmount = this.query.amount,
        originalCurrency = this.query.from
    )
}