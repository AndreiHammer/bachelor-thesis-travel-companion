package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationAction

sealed interface HotelListAction {
    data class OnHotelClick(val hotel: Hotel) : HotelListAction

    data object OnBackClick: HotelListAction
}