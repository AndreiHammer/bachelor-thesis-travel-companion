package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Bitmap
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result

class HotelLocationViewModel(
    private val hotelRepository: HotelRepositoryPlacesApi
) : ViewModel() {

    private val _state = MutableStateFlow(HotelState())
    val hotelState: StateFlow<HotelState> get() = _state

    fun getHotelDetails(locationName: String, country: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            hotelRepository.getHotelDetails(locationName, country) { result ->
                when (result) {
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Error fetching hotel details."
                        )
                    }
                    is Result.Success -> {
                        val (hotel, photos) = result.data
                        _state.value = _state.value.copy(
                            isLoading = false,
                            hotel = hotel,
                            photos = photos
                        )
                    }
                }
            }
        }
    }

    data class HotelState(
        val isLoading: Boolean = false,
        val hotel: Hotel? = null,
        val photos: List<Bitmap> = emptyList(),
        val errorMessage: String? = null
    )
}