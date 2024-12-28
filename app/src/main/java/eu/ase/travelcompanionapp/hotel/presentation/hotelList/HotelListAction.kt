package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

sealed interface HotelListAction {
    data class OnHotelClick(val hotel: Hotel) : HotelListAction

    data object OnBackClick: HotelListAction
}