package eu.ase.travelcompanionapp.user.data.currencyApi

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyDto(
    val success: Boolean,
    val query: QueryDto,
    val info: InfoDto,
    val date: String,
    val result: Double
)

@Serializable
data class QueryDto(
    val from: String,
    val to: String,
    val amount: Double
)

@Serializable
data class InfoDto(
    val timestamp: Long,
    val rate: Double
)