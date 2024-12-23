package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.hotel.domain.Hotel
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryAmadeusApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import eu.ase.travelcompanionapp.core.domain.Result


class HotelListViewModel(
    private val hotelRepository: HotelRepositoryAmadeusApi
) : ViewModel() {

    private val _state = MutableStateFlow(HotelListState())
    val hotelState: StateFlow<HotelListState> get() = _state

    fun getHotelList(city: String, amenities: String, rating: String) {
        viewModelScope.launch {
            hotelRepository.searchHotelsByCity(city, amenities, rating) { result ->
                when (result) {
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Error fetching hotels list"
                        )
                    }
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            hotels = result.data
                        )
                    }
                }
            }
        }
    }


    data class HotelListState(
        val isLoading: Boolean = true,
        val hotels: List<Hotel> = emptyList(),
        val errorMessage: String? = null
    )
}