package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.core.domain.DateConverter
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelOffersViewModel(
    private val hotelRepositoryAmadeusApi: HotelRepositoryAmadeusApi
) : ViewModel() {

    private val _state = MutableStateFlow(HotelOffersState())
    val state: StateFlow<HotelOffersState> = _state

    fun getHotelOffers(hotelId: String, checkInDate: String, checkOutDate: String, adults: Int) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val dateConverter = DateConverter()
                val apiCheckInDate = dateConverter.displayDateToApiFormat(checkInDate)
                val apiCheckOutDate = dateConverter.displayDateToApiFormat(checkOutDate)

                hotelRepositoryAmadeusApi.searchHotelOffers(
                    hotelId,
                    apiCheckInDate,
                    apiCheckOutDate,
                    adults.toString()
                ) { result ->
                    when (result) {
                        is eu.ase.travelcompanionapp.core.domain.Result.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = result.error.name
                            )
                        }
                        is eu.ase.travelcompanionapp.core.domain.Result.Success -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                offers = result.data
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    data class HotelOffersState(
        val isLoading: Boolean = false,
        val offers: List<HotelOffer> = emptyList(),
        val error: String? = null
    )
}