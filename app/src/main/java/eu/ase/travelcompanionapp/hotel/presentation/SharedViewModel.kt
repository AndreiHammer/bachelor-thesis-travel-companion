package eu.ase.travelcompanionapp.hotel.presentation

import android.util.Log
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

    private val _selectedCheckInDate = MutableStateFlow("")
    val selectedCheckInDate = _selectedCheckInDate.asStateFlow()

    private val _selectedCheckOutDate = MutableStateFlow("")
    val selectedCheckOutDate = _selectedCheckOutDate.asStateFlow()

    private val _selectedAdults = MutableStateFlow(1)
    val selectedAdults = _selectedAdults.asStateFlow()

    fun onSelectAdults(adults: Int) {
        _selectedAdults.value = adults
    }

    fun onSelectDates(checkInDate: String, checkOutDate: String) {
        _selectedCheckInDate.value = checkInDate
        _selectedCheckOutDate.value = checkOutDate
    }

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
        Log.d("SharedViewModel", "Setting location: $location with range: $range")
        _selectedLocation.value = LocationState(location, range)
    }

    data class LocationState(
        val location: LatLng? = null,
        val range: Int = 0
    )
}