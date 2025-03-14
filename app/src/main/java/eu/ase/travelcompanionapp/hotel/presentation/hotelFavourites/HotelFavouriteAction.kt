package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites

import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

interface HotelFavouriteAction {
    data class OnHotelClick(val hotel: Hotel) : HotelFavouriteAction

    data class OnRemoveFavourite(val hotelId: String) : HotelFavouriteAction

    data object OnBackClick: HotelFavouriteAction
}