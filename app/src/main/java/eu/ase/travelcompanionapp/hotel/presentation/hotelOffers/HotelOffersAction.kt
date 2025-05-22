package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer

sealed interface HotelOffersAction {
    data object OnBackClick : HotelOffersAction

    data class OnBookNow(val offer: HotelOffer) : HotelOffersAction
    
    data class OnModifyBookingDetails(
        val hotelId: String,
        val checkInDate: String,
        val checkOutDate: String,
        val adults: Int
    ) : HotelOffersAction
    
    data object OnShowModifyDialog : HotelOffersAction
    
    data object OnDismissDialog : HotelOffersAction
}