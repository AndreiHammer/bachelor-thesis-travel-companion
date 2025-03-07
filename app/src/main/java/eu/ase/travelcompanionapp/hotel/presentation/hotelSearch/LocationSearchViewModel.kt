package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.core.domain.utils.DateConverter
import eu.ase.travelcompanionapp.core.domain.utils.LocationUtils
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationSearchViewModel(
    private val cityToIATACodeRepository: CityToIATACodeRepository
) : ViewModel() {

    private val dateConverter = DateConverter()
    private val locationUtils = LocationUtils()

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

    fun performNearbySearch(
        context: Context,
        sharedViewModel: SharedViewModel,
        radius: Int,
        onResult: (Boolean) -> Unit
    ) {
        val (currentDate, nextDay) = dateConverter.getCurrentAndNextDayDates()

        sharedViewModel.onSelectDates(currentDate, nextDay)
        sharedViewModel.onSelectAdults(2)

        locationUtils.getUserLocation(context) { location ->
            if (location != null) {
                val latLng = locationUtils.locationToLatLng(location)
                sharedViewModel.onSelectLocation(latLng, radius)
                Log.d("LocationSearchViewModel", "Starting nearby search with radius: $radius")
                // After setting location
                Log.d("LocationSearchViewModel", "Set location in SharedViewModel: $latLng, radius: $radius")
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}