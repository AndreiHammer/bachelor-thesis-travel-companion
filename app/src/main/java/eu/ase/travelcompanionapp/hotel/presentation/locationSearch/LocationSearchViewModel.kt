package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LocationSearchViewModel : ViewModel() {

    private var city: String = ""

    fun onCityChange(newCity: String) {
        city = newCity
    }

    private val _locationState = MutableStateFlow(LocationState())

    fun setLocation(location: LatLng, range: Int, sharedViewModel: SharedViewModel) {
        _locationState.value = LocationState(location, range)
        sharedViewModel.onSelectLocation(location, range)
    }

    data class LocationState(
        val location: LatLng? = null,
        val range: Int = 0
    )
}