package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.FavoriteEvent
import eu.ase.travelcompanionapp.core.domain.utils.FavoritesEventBus
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel

class HotelLocationViewModel(
    private val hotelRepository: HotelRepositoryPlacesApi,
    private val navController: NavHostController,
    private val sharedViewModel: SharedViewModel,
    private val favouriteHotelRepository: FavouriteHotelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HotelState())
    val hotelState: StateFlow<HotelState> get() = _state

    fun getHotelDetails(locationName: String, country: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val hotelSelected = sharedViewModel.selectedHotel.value
            val isFavourite = hotelSelected?.let {
                favouriteHotelRepository.isFavourite(it.hotelId)
            } ?: false
            
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
                            photos = photos,
                            isFavourite = isFavourite
                        )
                    }
                }
            }
        }
    }

    private fun toggleFavorite(hotel: Hotel, checkInDate: String, checkOutDate: String, adults: Int) {
        viewModelScope.launch {
            val currentIsFavourite = _state.value.isFavourite

            _state.value = _state.value.copy(
                isFavourite = !currentIsFavourite
            )

            try {
                if (currentIsFavourite) {
                    favouriteHotelRepository.removeFavourite(hotel.hotelId)
                } else {
                    favouriteHotelRepository.addFavourite(
                        hotel,
                        checkInDate.takeIf { it.isNotEmpty() },
                        checkOutDate.takeIf { it.isNotEmpty() },
                        adults.takeIf { it > 0 }
                    )
                }

                FavoritesEventBus.emitEvent(FavoriteEvent.CountChanged)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isFavourite = currentIsFavourite,
                    errorMessage = "Failed to update favorites: ${e.message}"
                )
            }
        }
    }

    fun handleAction(action: HotelLocationAction, hotel: Hotel) {
        when(action) {
            HotelLocationAction.OnBackClick -> {
                navController.popBackStack()
            }
            is HotelLocationAction.OnViewOfferClick -> {
                sharedViewModel.onSelectHotel(hotel)
                navController.navigate(
                    HotelRoute.HotelOffers(
                        hotelId = hotel.hotelId,
                        checkInDate = action.checkInDate,
                        checkOutDate = action.checkOutDate,
                        adults = action.adults
                    )
                )
            }
            is HotelLocationAction.OnFavouriteClick -> {
                toggleFavorite(
                    hotel = hotel,
                    checkInDate = action.checkInDate,
                    checkOutDate = action.checkOutDate,
                    adults = action.adults
                )
            }
        }
    }

    data class HotelState(
        val isLoading: Boolean = false,
        val hotel: Hotel? = null,
        val photos: List<Bitmap> = emptyList(),
        val errorMessage: String? = null,
        val isFavourite: Boolean = false
    )
}