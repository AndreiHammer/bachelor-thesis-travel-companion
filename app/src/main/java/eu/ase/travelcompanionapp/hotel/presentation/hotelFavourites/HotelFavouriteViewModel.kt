package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.utils.FavoriteEvent
import eu.ase.travelcompanionapp.core.domain.utils.FavoritesEventBus
import eu.ase.travelcompanionapp.hotel.domain.model.HotelWithBookingDetails
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HotelFavouriteViewModel(
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel,
    private val favouriteHotelRepository: FavouriteHotelRepository
) : ViewModel() {
    private val _hotelState = MutableStateFlow(HotelFavouriteState())
    val hotelState = _hotelState.asStateFlow()

    fun getHotelFavourites() {
        viewModelScope.launch {
            _hotelState.value = _hotelState.value.copy(isLoading = true)

            favouriteHotelRepository.getFavouriteHotels().collectLatest { hotels ->
                val hotelsWithDetails = mutableListOf<HotelWithBookingDetails>()

                for (hotel in hotels) {
                    val bookingDetails = favouriteHotelRepository.getBookingDetails(hotel.hotelId)
                    hotelsWithDetails.add(HotelWithBookingDetails(hotel, bookingDetails))
                }

                _hotelState.value = _hotelState.value.copy(
                    hotelsWithDetails = hotelsWithDetails,
                    isLoading = false,
                    errorMessage = if (hotels.isEmpty()) "No favorite hotels found yet" else null
                )
            }
        }
    }

    private fun removeFavourite(hotelId: String) {
        viewModelScope.launch {
            favouriteHotelRepository.removeFavourite(hotelId)

            FavoritesEventBus.emitEvent(FavoriteEvent.CountChanged)

            getHotelFavourites()
        }
    }

    fun handleAction(action: HotelFavouriteAction) {
        when (action) {
            is HotelFavouriteAction.OnHotelClick -> {
                viewModelScope.launch {
                    sharedViewModel.onSelectHotel(action.hotel)

                    val bookingDetails = favouriteHotelRepository.getBookingDetails(action.hotel.hotelId)
                    if (bookingDetails != null) {
                        sharedViewModel.updateBookingDetailsFromFavourite(
                            bookingDetails.checkInDate,
                            bookingDetails.checkOutDate,
                            bookingDetails.adults
                        )
                    }

                    navController.navigate(
                        HotelRoute.HotelDetail(action.hotel.hotelId)
                    )
                }
            }
            is HotelFavouriteAction.OnBackClick -> {
                navController.popBackStack()
            }
            is HotelFavouriteAction.OnRemoveFavourite -> {
                removeFavourite(action.hotelId)
            }
        }
    }
}
data class HotelFavouriteState(
    val hotelsWithDetails: List<HotelWithBookingDetails> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)