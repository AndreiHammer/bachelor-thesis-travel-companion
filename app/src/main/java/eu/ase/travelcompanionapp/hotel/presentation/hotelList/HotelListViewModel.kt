package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository


class HotelListViewModel(
    private val hotelRepository: HotelRepositoryAmadeusApi,
    private val cityToIATACodeRepository: CityToIATACodeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HotelListState())
    val hotelState: StateFlow<HotelListState> get() = _state

    fun getHotelListByCity(city: String, amenities: String, rating: String) {
        val iataCode = cityToIATACodeRepository.getIATACode(city)
        if (iataCode != null) {
            viewModelScope.launch {
                hotelRepository.searchHotelsByCity(iataCode, amenities, rating) { result ->
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
        } else {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Invalid city. No IATA code found."
            )
        }
    }

    fun getHotelListByLocation(latitude: Double, longitude: Double, radius: Int, amenities: String, rating: String) {
        viewModelScope.launch {
            hotelRepository.searchHotelsByLocation(
                latitude,
                longitude,
                radius,
                amenities,
                rating
            ) { result ->
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

    fun onAction(action: HotelListAction) {
        when (action) {
            is HotelListAction.OnHotelClick -> {
                // Navigate to hotel details screen
            }

            HotelListAction.OnBackClick -> {

            }
        }
    }


    data class HotelListState(
        val isLoading: Boolean = true,
        val hotels: List<Hotel> = emptyList(),
        val errorMessage: String? = null,
        val badIataCode : Boolean = false
    )
}