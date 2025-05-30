package eu.ase.travelcompanionapp.recommendation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.recommendation.domain.RecommendationRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.recommendation.domain.model.CityDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecommendationViewModel(
    private val navController: NavController,
    private val recommendationRepository: RecommendationRepository,
    private val hotelThumbnailRepository: HotelThumbnailRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state = _state.asStateFlow()

    private val _hotelImages = MutableStateFlow<Map<String, Bitmap?>>(emptyMap())
    val hotelImages: StateFlow<Map<String, Bitmap?>> = _hotelImages.asStateFlow()

    private val _explanations = MutableStateFlow<Map<String, String>>(emptyMap())
    val explanations: StateFlow<Map<String, String>> = _explanations.asStateFlow()
    
    private val _destinationExplanations = MutableStateFlow<Map<String, String>>(emptyMap())
    val destinationExplanations: StateFlow<Map<String, String>> = _destinationExplanations.asStateFlow()

    init {
        getRecommendations()
        getDestinationRecommendations()
    }

    fun getRecommendations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                relatedHotels = emptyList(),
                relatedHotelId = null,
                isLoadingRelated = false
            )
            
            try {
                recommendationRepository.getPersonalizedRecommendations(10)
                    .catch { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load recommendations: ${error.message}"
                        )
                    }
                    .collectLatest { recommendations ->
                        _state.value = _state.value.copy(
                            recommendations = recommendations,
                            isLoading = false,
                            errorMessage = if (recommendations.isEmpty()) {
                                "No recommendations available. Try adding some hotels to favorites or make some bookings first."
                            } else null
                        )
                        
                        if (recommendations.isNotEmpty()) {
                            loadHotelImages(recommendations)
                            loadExplanations(recommendations)
                        }
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load recommendations: ${e.message}"
                )
            }
        }
    }
    
    fun getDestinationRecommendations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingDestinations = true)
            
            try {
                recommendationRepository.getDestinationRecommendations(8)
                    .catch { error ->
                        _state.value = _state.value.copy(
                            isLoadingDestinations = false,
                            destinationErrorMessage = "Failed to load destination recommendations: ${error.message}"
                        )
                    }
                    .collectLatest { destinations ->
                        _state.value = _state.value.copy(
                            destinationRecommendations = destinations,
                            isLoadingDestinations = false,
                            destinationErrorMessage = null
                        )
                        
                        if (destinations.isNotEmpty()) {
                            loadDestinationExplanations(destinations)
                        }
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingDestinations = false,
                    destinationErrorMessage = "Failed to load destination recommendations: ${e.message}"
                )
            }
        }
    }

    fun getSimilarHotels(hotel: Hotel) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingRelated = true,
                relatedHotelId = hotel.hotelId
            )
            
            try {
                val similarHotels = recommendationRepository.getSimilarHotels(hotel.hotelId, 5)
                
                _state.value = _state.value.copy(
                    relatedHotels = similarHotels,
                    isLoadingRelated = false
                )
                
                if (similarHotels.isNotEmpty()) {
                    loadHotelImages(similarHotels)
                    loadExplanations(similarHotels)
                }
                
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingRelated = false,
                    errorMessage = "Failed to load similar hotels: ${e.message}"
                )
            }
        }
    }
    
    fun onSearchDestination(destination: CityDestination) {
        // Navigate to hotel search with the selected city
        sharedViewModel.onSelectCity(destination.cityName)
        navController.navigate(HotelRoute.HotelListCity(destination.cityName))
    }

    private fun loadHotelImages(hotels: List<Hotel>) {
        viewModelScope.launch {
            hotels.forEach { hotel ->
                try {
                    // Set loading state
                    _hotelImages.value = _hotelImages.value + (hotel.hotelId to null)
                    
                    // Load image
                    val image = hotelThumbnailRepository.getHotelThumbnail(hotel)
                    _hotelImages.value = _hotelImages.value + (hotel.hotelId to image)
                } catch (e: Exception) {
                    // Keep null placeholder on error
                    _hotelImages.value = _hotelImages.value + (hotel.hotelId to null)
                }
            }
        }
    }

    private fun loadExplanations(hotels: List<Hotel>) {
        viewModelScope.launch {
            val newExplanations = mutableMapOf<String, String>()
            
            hotels.forEach { hotel ->
                try {
                    val explanation = recommendationRepository.getRecommendationExplanation(hotel)
                    newExplanations[hotel.hotelId] = explanation
                } catch (e: Exception) {
                    newExplanations[hotel.hotelId] = "Recommended based on your preferences"
                }
            }
            
            _explanations.value = _explanations.value + newExplanations
        }
    }
    
    private fun loadDestinationExplanations(destinations: List<CityDestination>) {
        viewModelScope.launch {
            val newExplanations = mutableMapOf<String, String>()
            
            destinations.forEach { destination ->
                try {
                    val explanation = recommendationRepository.getDestinationExplanation(destination)
                    newExplanations[destination.iataCode] = explanation
                } catch (e: Exception) {
                    newExplanations[destination.iataCode] = "Popular travel destination"
                }
            }
            
            _destinationExplanations.value = _destinationExplanations.value + newExplanations
        }
    }

    fun navigateToHotelDetails(hotel: Hotel) {
        sharedViewModel.onSelectHotel(hotel)
        navController.navigate(HotelRoute.HotelDetail(hotel.hotelId))
    }

}

data class RecommendationState(
    val recommendations: List<Hotel> = emptyList(),
    val relatedHotels: List<Hotel> = emptyList(),
    val relatedHotelId: String? = null,
    val isLoading: Boolean = false,
    val isLoadingRelated: Boolean = false,
    val errorMessage: String? = null,
    val destinationRecommendations: List<CityDestination> = emptyList(),
    val isLoadingDestinations: Boolean = false,
    val destinationErrorMessage: String? = null
)