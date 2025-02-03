package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationSearchViewModel(
    private val cityToIATACodeRepository: CityToIATACodeRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val _locationState = MutableStateFlow(LocationState())

    fun setLocation(location: LatLng, range: Int, sharedViewModel: SharedViewModel) {
        _locationState.value = LocationState(location, range)
        sharedViewModel.onSelectLocation(location, range)
    }

    data class LocationState(
        val location: LatLng? = null,
        val range: Int = 0
    )

    fun fetchSuggestions() {
        _suggestions.value = cityToIATACodeRepository.getCitySuggestions()
    }
}