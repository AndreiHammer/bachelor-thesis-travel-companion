package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer

sealed interface HotelOffersAction {
    data object OnBackClick : HotelOffersAction

    data class OnBookNow(val offer: HotelOffer) : HotelOffersAction
}