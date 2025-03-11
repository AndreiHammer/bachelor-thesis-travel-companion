package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelFavouriteViewModel(
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val _hotelState = MutableStateFlow(HotelFavouriteState())
    val hotelState = _hotelState.asStateFlow()

    // This will be replaced with actual database query later
    fun getHotelFavourites() {
        viewModelScope.launch {
            _hotelState.value = _hotelState.value.copy(isLoading = true)

            // Simulate network delay for testing UI
            delay(500)

            // For now, return empty list
            _hotelState.value = _hotelState.value.copy(
                hotels = emptyList(),
                isLoading = false,
                errorMessage = "No favorite hotels found yet" // Provide a more user-friendly message
            )
        }
    }

    fun handleAction(action: HotelFavouriteAction) {
        when (action) {
            is HotelFavouriteAction.OnHotelClick -> {
                sharedViewModel.onSelectHotel(action.hotel)
                navController.navigate(
                    HotelRoute.HotelDetail(action.hotel.hotelId)
                )
            }
            is HotelFavouriteAction.OnBackClick -> {
                navController.popBackStack()
            }
        }
    }
}
data class HotelFavouriteState(
    val hotels: List<Hotel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)