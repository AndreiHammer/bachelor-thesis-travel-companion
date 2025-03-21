package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HotelOffersViewModel(
    private val hotelRepositoryAmadeusApi: HotelRepositoryAmadeusApi,
    private val navController: NavHostController
) : ViewModel() {

    private val _state = MutableStateFlow(HotelOffersState())
    val state: StateFlow<HotelOffersState> = _state

    fun getHotelOffers(hotelId: String, checkInDate: String, checkOutDate: String, adults: Int) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val dateUtils = DateUtils()
                val apiCheckInDate = dateUtils.displayDateToApiFormat(checkInDate)
                val apiCheckOutDate = dateUtils.displayDateToApiFormat(checkOutDate)

                hotelRepositoryAmadeusApi.searchHotelOffers(
                    hotelId,
                    apiCheckInDate,
                    apiCheckOutDate,
                    adults.toString()
                ) { result ->
                    when (result) {
                        is Result.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = result.error.name
                            )
                        }
                        is Result.Success -> {
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

    fun handleAction(action: HotelOffersAction) {
        when(action) {
            HotelOffersAction.OnBackClick -> {
                navController.popBackStack()
            }
            HotelOffersAction.OnBookNow -> {

            }
        }
    }

    data class HotelOffersState(
        val isLoading: Boolean = false,
        val offers: List<HotelOffer> = emptyList(),
        val error: String? = null
    )
}