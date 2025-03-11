package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails

sealed interface HotelLocationAction {
    data object OnBackClick: HotelLocationAction

    data class OnViewOfferClick(val checkInDate: String, val checkOutDate: String, val adults: Int): HotelLocationAction

    data object OnFavouriteClick: HotelLocationAction
}