package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

sealed interface HotelOffersAction {
    data object OnBackClick : HotelOffersAction

    data object OnBookNow : HotelOffersAction
}