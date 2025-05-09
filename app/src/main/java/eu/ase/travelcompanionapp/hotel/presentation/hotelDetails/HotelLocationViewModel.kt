package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.EventBus
import eu.ase.travelcompanionapp.core.domain.utils.FavouriteEvent
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristSharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HotelLocationViewModel(
    private val hotelRepository: HotelRepositoryPlacesApi,
    private val navController: NavHostController,
    private val sharedViewModel: SharedViewModel,
    private val favouriteHotelRepository: FavouriteHotelRepository,
    private val touristAttractionRepository: TouristAttractionRepositoryAmadeusApi
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(HotelState())
    val hotelState: StateFlow<HotelState> get() = _state

    private val _touristAttractionsState = MutableStateFlow(TouristAttractionsState())
    val touristAttractionsState: StateFlow<TouristAttractionsState> = _touristAttractionsState.asStateFlow()

    private val touristAttractionsSharedViewModel: TouristSharedViewModel by inject()

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

                        val latitude = hotel.latitude
                        val longitude = hotel.longitude

                        fetchNearbyAttractions(latitude, longitude)
                    }
                }
            }
        }
    }

    private fun fetchNearbyAttractions(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _touristAttractionsState.update { it.copy(isLoading = true, error = null) }

            touristAttractionRepository.searchTouristAttractionsByLocation(latitude, longitude) { result ->
                when (result) {
                    is Result.Error -> {
                        _touristAttractionsState.update { it.copy(
                            isLoading = false,
                            error = "Failed to load tourist attractions",
                            attractions = emptyList()
                        )}
                    }
                    is Result.Success -> {
                        val attractions = result.data

                        _touristAttractionsState.update { it.copy(
                            isLoading = false,
                            attractions = attractions,
                            error = null
                        )}
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

    fun setupTouristAttractionsNavigation(latitude: Double, longitude: Double) {
        touristAttractionsSharedViewModel.setLocation(latitude, longitude)
    }
    
    fun setupTouristAttractionDetailsNavigation(attraction: TouristAttraction) {
        attraction.id?.let { id ->
            touristAttractionsSharedViewModel.setSelectedAttraction(attraction)
            touristAttractionsSharedViewModel.setSelectedAttractionId(id)
        }
    }

    data class HotelState(
        val isLoading: Boolean = false,
        val hotel: Hotel? = null,
        val photos: List<Bitmap> = emptyList(),
        val errorMessage: String? = null,
        val isFavourite: Boolean = false
    )

    data class TouristAttractionsState(
        val isLoading: Boolean = false,
        val attractions: List<TouristAttraction> = emptyList(),
        val error: String? = null
    )
}