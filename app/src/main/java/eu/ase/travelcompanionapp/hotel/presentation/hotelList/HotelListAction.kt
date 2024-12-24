package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import eu.ase.travelcompanionapp.hotel.domain.Hotel

sealed interface HotelListAction {
    data class OnHotelClick(val hotel: Hotel) : HotelListAction
}