package eu.ase.travelcompanionapp.hotel.domain.model

import eu.ase.travelcompanionapp.user.domain.model.Currency

data class HotelPrice(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val noOffers: Boolean = false,
    val originalPrice: Double? = null,
    val originalCurrency: String? = null,
    val convertedPrice: Currency? = null
)
