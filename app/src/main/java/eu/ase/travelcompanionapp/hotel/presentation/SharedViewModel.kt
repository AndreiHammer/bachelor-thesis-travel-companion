package eu.ase.travelcompanionapp.hotel.presentation

import androidx.lifecycle.ViewModel
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel: ViewModel() {
    private val _selectedHotel = MutableStateFlow<Hotel?>(null)
    val selectedHotel = _selectedHotel.asStateFlow()

    private val _selectedCity = MutableStateFlow<String>("")
    val selectedCity = _selectedCity.asStateFlow()

    fun onSelectHotel(hotel: Hotel?) {
        _selectedHotel.value = hotel
    }

    fun onSelectCity(city: String) {
        _selectedCity.value = city
    }


}