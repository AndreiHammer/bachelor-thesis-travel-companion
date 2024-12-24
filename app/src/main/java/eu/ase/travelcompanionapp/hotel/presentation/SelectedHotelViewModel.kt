package eu.ase.travelcompanionapp.hotel.presentation

import androidx.lifecycle.ViewModel
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectedHotelViewModel: ViewModel() {
    private val _selectedHotel = MutableStateFlow<Hotel?>(null)
    val selectedHotel = _selectedHotel.asStateFlow()

    fun onSelectHotel(hotel: Hotel?) {
        _selectedHotel.value = hotel
    }


}