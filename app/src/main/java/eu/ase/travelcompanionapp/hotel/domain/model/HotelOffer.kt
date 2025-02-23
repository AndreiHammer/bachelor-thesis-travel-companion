package eu.ase.travelcompanionapp.hotel.domain.model

import eu.ase.travelcompanionapp.hotel.domain.model.components.Offer

data class HotelOffer(
    val type: String?,
    val hotel: Hotel?,
    val available: Boolean?,
    val offers: List<Offer> = emptyList(),
)