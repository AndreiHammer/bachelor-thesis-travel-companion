package eu.ase.travelcompanionapp.touristattractions.presentation

import androidx.lifecycle.ViewModel
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TouristSharedViewModel : ViewModel() {
    private val _selectedLatitude = MutableStateFlow(0.0)
    val selectedLatitude = _selectedLatitude.asStateFlow()

    private val _selectedLongitude = MutableStateFlow(0.0)
    val selectedLongitude = _selectedLongitude.asStateFlow()

    private val _selectedAttractionId = MutableStateFlow<String?>(null)
    val selectedAttractionId = _selectedAttractionId.asStateFlow()

    private val _selectedAttraction = MutableStateFlow<TouristAttraction?>(null)
    val selectedAttraction = _selectedAttraction.asStateFlow()

    fun setLocation(latitude: Double, longitude: Double) {
        _selectedLatitude.value = latitude
        _selectedLongitude.value = longitude
    }

    fun setSelectedAttractionId(attractionId: String) {
        _selectedAttractionId.value = attractionId
    }

    fun setSelectedAttraction(attraction: TouristAttraction) {
        _selectedAttraction.value = attraction
    }
} 