package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import androidx.lifecycle.ViewModel

class LocationSearchViewModel : ViewModel() {

    var city: String = ""
        private set

    fun onCityChange(newCity: String) {
        city = newCity
    }

}