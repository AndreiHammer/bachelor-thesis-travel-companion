package eu.ase.travelcompanionapp.core.domain.utils

import eu.ase.travelcompanionapp.hotel.domain.model.Hotel

object CrossGraphDataHolder {
    var tempHotel: Hotel? = null
    var tempCheckInDate: String? = null
    var tempCheckOutDate: String? = null
    var tempAdults: Int? = null

    fun clearTempData() {
        tempHotel = null
        tempCheckInDate = null
        tempCheckOutDate = null
        tempAdults = null
    }
}