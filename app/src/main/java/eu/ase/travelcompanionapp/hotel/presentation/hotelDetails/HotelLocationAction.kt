package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails

sealed interface HotelLocationAction {
    data object OnBackClick: HotelLocationAction
}