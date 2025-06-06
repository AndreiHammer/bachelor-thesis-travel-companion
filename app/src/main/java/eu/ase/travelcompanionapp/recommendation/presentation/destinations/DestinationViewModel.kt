package eu.ase.travelcompanionapp.recommendation.presentation.destinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.DestinationRoute
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination
import eu.ase.travelcompanionapp.recommendation.domain.model.RecommendedDestinations
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationApiRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationThumbnailRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DestinationViewModel(
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel,
    private val userProfileRepository: UserProfileRepository,
    private val destinationApiRepository: DestinationApiRepository,
    private val destinationThumbnailRepository: DestinationThumbnailRepository
) : ViewModel() {
    val hotelFilters = sharedViewModel

    private val _state = MutableStateFlow(DestinationState())
    val state: StateFlow<DestinationState> = _state.asStateFlow()

    private val _showHotelSearchDialog = MutableStateFlow(false)
    val showHotelSearchDialog: StateFlow<Boolean> = _showHotelSearchDialog.asStateFlow()

    private val _selectedCityForHotelSearch = MutableStateFlow("")
    val selectedCityForHotelSearch: StateFlow<String> = _selectedCityForHotelSearch.asStateFlow()

    private val _destinationImages = MutableStateFlow<Map<String, Bitmap>>(emptyMap())

    fun setInitialRecommendations(recommendations: RecommendedDestinations?) {
        _state.value = _state.value.copy(
            recommendations = recommendations,
            isLoading = recommendations == null
        )

        recommendations?.destinations?.forEach { destination ->
            loadDestinationImage(destination)
        }
    }

    fun getRecommendations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val userProfile = userProfileRepository.getUserProfilePreview()
                
                when (val result = destinationApiRepository.getRecommendedDestinations(userProfile.userId)) {
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            recommendations = result.data,
                            errorMessage = null
                        )
                        result.data.destinations.forEach { destination ->
                            loadDestinationImage(destination)
                        }
                    }
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            DataError.Remote.NO_INTERNET ->
                                "No internet connection. Please check your network."
                            DataError.Remote.REQUEST_TIMEOUT ->
                                "Request timed out. The AI is working hard to generate your recommendations. Please try again."
                            DataError.Remote.SERVER ->
                                "Server error. Please try again later."
                            DataError.Remote.SERIALIZATION ->
                                "Data parsing error. Please try again."
                            DataError.Remote.UNKNOWN ->
                                "Network error occurred. The server might be processing your request. Please try again."
                            else -> "Failed to get recommendations: ${result.error}"
                        }
                        
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    fun navigateToDestinationDetail(destination: Destination) {
        loadDestinationImage(destination)
        
        navController.navigate(
            DestinationRoute.DestinationDetail(
                city = destination.city,
                country = destination.country,
                iataCode = destination.iataCode,
                continent = destination.continent,
                latitude = destination.latitude,
                longitude = destination.longitude,
                description = destination.description,
                seasonScore = destination.seasonScore,
                budgetLevel = destination.budgetLevel,
                matchReasons = destination.matchReasons,
                bestFor = destination.bestFor,
                popularAttractions = destination.popularAttractions
            )
        )
    }

    fun showHotelSearchDialog(cityName: String) {
        _selectedCityForHotelSearch.value = cityName
        _showHotelSearchDialog.value = true
    }

    fun hideHotelSearchDialog() {
        _showHotelSearchDialog.value = false
        _selectedCityForHotelSearch.value = ""
    }

    fun searchHotelsWithFilters(
        cityName: String,
        checkInDate: String,
        checkOutDate: String,
        adults: Int,
        ratings: Set<Int>,
        amenities: Set<String>
    ) {
        sharedViewModel.onSelectCity(cityName)
        sharedViewModel.onSelectDates(checkInDate, checkOutDate)
        sharedViewModel.onSelectAdults(adults)
        sharedViewModel.onSelectRating(ratings)
        sharedViewModel.onSelectAmenities(amenities)

        hideHotelSearchDialog()
        viewModelScope.launch {
            navController.navigate(HotelRoute.HotelListCity(city = cityName))
        }
    }

    private fun loadDestinationImage(destination: Destination) {
        val imageKey = getDestinationImageKey(destination)

        if (_destinationImages.value.containsKey(imageKey)) {
            return
        }
        
        viewModelScope.launch {
            try {
                val bitmap = destinationThumbnailRepository.getDestinationThumbnail(destination)
                if (bitmap != null) {
                    _destinationImages.update { currentImages ->
                        currentImages + (imageKey to bitmap)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun getDestinationImage(destination: Destination): Bitmap? {
        val imageKey = getDestinationImageKey(destination)
        return _destinationImages.value[imageKey]
    }

    fun loadDestinationImageForDetail(destination: Destination) {
        loadDestinationImage(destination)
    }

    private fun getDestinationImageKey(destination: Destination): String {
        return "${destination.city}_${destination.country}"
    }
}

data class DestinationState(
    val isLoading: Boolean = false,
    val recommendations: RecommendedDestinations? = null,
    val errorMessage: String? = null
)
