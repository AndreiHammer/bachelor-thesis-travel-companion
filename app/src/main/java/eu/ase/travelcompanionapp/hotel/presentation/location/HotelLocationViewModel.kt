package eu.ase.travelcompanionapp.hotel.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Bitmap
import eu.ase.travelcompanionapp.hotel.data.placesApi.HotelRepositoryImpl
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelLocationViewModel(
    private val hotelRepository: HotelRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow(HotelState())

    val hotelState: StateFlow<HotelState> get() = _state

    fun getHotelDetails(hotelName: String, country: String) {
        viewModelScope.launch {
            hotelRepository.getHotelDetails(hotelName, country) { result ->
                when (result) {
                    is eu.ase.travelcompanionapp.core.domain.Result.Error -> {
                        _state.value = _state.value.copy(errorMessage = "Error fetching hotel details.")
                    }
                    is eu.ase.travelcompanionapp.core.domain.Result.Success -> {
                        val (hotel, photos) = result.data
                        _state.value = _state.value.copy(hotel = hotel, photos = photos)
                    }
                }
            }
        }
    }

    data class HotelState(
        val hotel: Hotel? = null,
        val photos: List<Bitmap> = emptyList(),
        val errorMessage: String? = null
    )
}