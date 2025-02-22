package eu.ase.travelcompanionapp.hotel.presentation

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _selectedHotel = MutableStateFlow<Hotel?>(null)
    val selectedHotel = _selectedHotel.asStateFlow()

    private val _selectedCity = MutableStateFlow("")
    val selectedCity = _selectedCity.asStateFlow()

    private val _selectedRatings = MutableStateFlow(setOf<Int>())
    val selectedRatings = _selectedRatings.asStateFlow()

    private val _selectedAmenities = MutableStateFlow(setOf<String>())
    val selectedAmenities = _selectedAmenities.asStateFlow()

    private val _selectedLocation = MutableStateFlow(LocationState())
    val selectedLocation = _selectedLocation.asStateFlow()

    fun onSelectHotel(hotel: Hotel?) {
        _selectedHotel.value = hotel
    }

    fun onSelectCity(city: String) {
        _selectedCity.value = city
    }

    fun onSelectRating(ratings: Set<Int>) {
        _selectedRatings.value = ratings
    }

    fun onSelectAmenities(amenities: Set<String>) {
        _selectedAmenities.value = amenities
    }

    fun onSelectLocation(location: LatLng, range: Int) {
        _selectedLocation.value = LocationState(location, range)
    }

    data class LocationState(
        val location: LatLng? = null,
        val range: Int = 0
    )
}