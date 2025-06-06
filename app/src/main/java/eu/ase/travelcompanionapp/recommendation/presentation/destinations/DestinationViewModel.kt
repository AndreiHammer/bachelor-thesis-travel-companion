package eu.ase.travelcompanionapp.recommendation.presentation.destinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.DestinationRoute
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination
import eu.ase.travelcompanionapp.recommendation.domain.model.RecommendedDestinations
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationApiRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DestinationViewModel(
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel,
    private val userProfileRepository: UserProfileRepository,
    private val destinationApiRepository: DestinationApiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DestinationState())
    val state: StateFlow<DestinationState> = _state.asStateFlow()

    fun setInitialRecommendations(recommendations: RecommendedDestinations?) {
        _state.value = _state.value.copy(
            recommendations = recommendations,
            isLoading = recommendations == null
        )
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

    fun searchHotelsInCity(cityName: String) {
        sharedViewModel.onSelectCity(cityName)
        navController.navigate(HotelRoute.HotelListCity(city = cityName))
    }
}

data class DestinationState(
    val isLoading: Boolean = false,
    val recommendations: RecommendedDestinations? = null,
    val errorMessage: String? = null
)