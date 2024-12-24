package eu.ase.travelcompanionapp.hotel.presentation.location

sealed interface HotelLocationAction {
    data object OnBackClick: HotelLocationAction
}