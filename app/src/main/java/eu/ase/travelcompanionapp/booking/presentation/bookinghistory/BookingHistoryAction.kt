package eu.ase.travelcompanionapp.booking.presentation.bookinghistory

import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

sealed interface BookingHistoryAction {
    data class OnBookingClick(val booking: BookingInfo, val hotel: Hotel) : BookingHistoryAction

    data object OnBackClick : BookingHistoryAction
    
    data class OnTabSelected(val index: Int) : BookingHistoryAction
}