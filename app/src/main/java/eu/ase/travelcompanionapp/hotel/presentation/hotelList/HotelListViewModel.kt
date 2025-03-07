package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelListViewModel(
    private val hotelRepository: HotelRepositoryAmadeusApi,
    private val cityToIATACodeRepository: CityToIATACodeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HotelListState())
    val hotelState: StateFlow<HotelListState> get() = _state


    fun getHotelListByCity(city: String, amenities: String, rating: String) {
        
        val iataCode = cityToIATACodeRepository.getIATACode(city)
        if (iataCode == null) {
            _state.value = HotelListState(
                hotels = emptyList(),
                errorMessage = "Invalid city code",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = HotelListState(
                hotels = emptyList(),
                errorMessage = null,
                isLoading = true
            )
            
            hotelRepository.searchHotelsByCity(iataCode, amenities, rating) { result ->
                when (result) {
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            DataError.Remote.NOT_FOUND -> "No hotels found in $city"
                            DataError.Remote.UNKNOWN -> "City not found"
                            else -> "Error fetching hotels"
                        }
                        _state.value = HotelListState(
                            hotels = emptyList(),
                            errorMessage = errorMessage,
                            isLoading = false
                        )
                    }
                    is Result.Success -> {
                        _state.value = HotelListState(
                            hotels = result.data,
                            errorMessage = if (result.data.isEmpty()) "No hotels found in $city" else null,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getHotelListByLocation(latitude: Double, longitude: Double, radius: Int, amenities: String, rating: String) {
        viewModelScope.launch {
            _state.value = HotelListState(
                hotels = emptyList(),
                errorMessage = null,
                isLoading = true
            )
            
            hotelRepository.searchHotelsByLocation(
                latitude,
                longitude,
                radius,
                amenities,
                rating
            ) { result ->
                when (result) {
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            DataError.Remote.NOT_FOUND -> "No hotels found in this area"
                            DataError.Remote.UNKNOWN -> "Invalid area"
                            else -> "Error fetching hotels"
                        }
                        _state.value = HotelListState(
                            hotels = emptyList(),
                            errorMessage = errorMessage,
                            isLoading = false
                        )
                    }
                    is Result.Success -> {
                        _state.value = HotelListState(
                            hotels = result.data,
                            errorMessage = if (result.data.isEmpty()) "No hotels found in this area" else null,
                            isLoading = false
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
        val hotels: List<Hotel> = emptyList(),
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )
}