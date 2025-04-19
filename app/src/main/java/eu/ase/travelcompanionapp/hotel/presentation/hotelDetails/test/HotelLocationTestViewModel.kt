package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.utils.EventBus
import eu.ase.travelcompanionapp.core.domain.utils.FavouriteEvent
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class HotelLocationTestViewModel(
    private val navController: NavHostController,
    private val sharedViewModel: SharedViewModel,
    private val favouriteHotelRepository: FavouriteHotelRepository
) : ViewModel(){

    private val _state = MutableStateFlow(HotelLocationViewModel.HotelState())
    val hotelState: StateFlow<HotelLocationViewModel.HotelState> get() = _state

    fun getHotelDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val hotelSelected = sharedViewModel.selectedHotel.value

            val isFavourite = hotelSelected?.let {
                favouriteHotelRepository.isFavourite(it.hotelId)
            } ?: false

            if(hotelSelected != null) {
                // Check if location is imprecise (near 0,0 or null)
                val hasImpreciseLocation = hotelSelected.latitude == 0.0 || 
                                          hotelSelected.longitude == 0.0 ||
                                          (hotelSelected.latitude.absoluteValue < 0.001 && 
                                           hotelSelected.longitude.absoluteValue < 0.001)
                
                // Create a copy of the hotel with precise location if needed
                val hotelWithPreciseLocation = if (hasImpreciseLocation) {
                    // Use country code to determine approximate coordinates for testing
                    val (lat, lng) = getDefaultCoordinatesForCountry(hotelSelected.countryCode)
                    hotelSelected.copy(
                        latitude = lat,
                        longitude = lng
                    )
                } else {
                    hotelSelected
                }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    hotel = hotelWithPreciseLocation,
                    isFavourite = isFavourite,
                    photos = emptyList()
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error fetching hotel details."
                )
            }
        }
    }

    // Helper function to provide default coordinates based on country code
    private fun getDefaultCoordinatesForCountry(countryCode: String): Pair<Double, Double> {
        return when (countryCode) {
            "FR" -> 48.8566 to 2.3522  // Paris, France
            "US" -> 40.7128 to -74.0060  // New York, USA
            "GB" -> 51.5074 to -0.1278  // London, UK
            "IT" -> 41.9028 to 12.4964  // Rome, Italy
            "ES" -> 40.4168 to -3.7038  // Madrid, Spain
            "DE" -> 52.5200 to 13.4050  // Berlin, Germany
            "JP" -> 35.6762 to 139.6503  // Tokyo, Japan
            "AU" -> -33.8688 to 151.2093  // Sydney, Australia
            "CA" -> 43.6532 to -79.3832  // Toronto, Canada
            "BR" -> -22.9068 to -43.1729  // Rio de Janeiro, Brazil
            "RO" -> 44.4268 to 26.1025  // Bucharest, Romania
            else -> 51.5074 to -0.1278  // Default to London if country code not recognized
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

                EventBus.favourites.emitEvent(FavouriteEvent.CountChanged)

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
}