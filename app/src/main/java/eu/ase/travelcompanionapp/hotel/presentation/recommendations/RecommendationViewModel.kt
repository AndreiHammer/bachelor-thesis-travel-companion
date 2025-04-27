package eu.ase.travelcompanionapp.hotel.presentation.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.RecommendationRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay

class RecommendationViewModel(
    private val navController: NavController,
    private val recommendationRepository: RecommendationRepository,
    private val hotelThumbnailRepository: HotelThumbnailRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    
    private val _state = MutableStateFlow(RecommendationState())
    val state = _state.asStateFlow()
    
    private val _hotelImages = MutableStateFlow<Map<String, Bitmap?>>(emptyMap())
    //val hotelImages: StateFlow<Map<String, Bitmap?>> = _hotelImages.asStateFlow()
    
    private val _explanations = MutableStateFlow<Map<String, String>>(emptyMap())
    val explanations: StateFlow<Map<String, String>> = _explanations.asStateFlow()
    
    init {
        getRecommendations()
    }
    
    fun getRecommendations() {

        val currentJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                withTimeout(30000) {
                    recommendationRepository.getPersonalizedRecommendations().collect { recommendations ->
                        _state.value = _state.value.copy(
                            recommendations = recommendations,
                            isLoading = false,
                            errorMessage = if (recommendations.isEmpty()) "No recommendations found. Try adding hotels to favorites." else null
                        )

                        if (recommendations.isNotEmpty()) {
                            loadHotelImages(recommendations)
                            loadExplanations(recommendations)
                        }
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load recommendations: ${e.message}"
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            delay(45000) // 45 seconds
            if (_state.value.isLoading) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Loading recommendations took too long. Please try again."
                )
                currentJob.cancel()
            }
        }
    }
    
    fun getSimilarHotels(hotel: Hotel) {
        // Cancel any previous job
        val currentJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingRelated = true,
                relatedHotels = emptyList(),
                relatedHotelId = hotel.hotelId
            )

            try {
                // Use a timeout
                val similarHotels = withTimeout(30000) { // 30 second timeout
                    recommendationRepository.getSimilarHotels(hotel.hotelId)
                }

                _state.value = _state.value.copy(
                    relatedHotels = similarHotels,
                    isLoadingRelated = false,
                    errorMessage = if (similarHotels.isEmpty()) "No similar hotels found" else null
                )

                if (similarHotels.isNotEmpty()) {
                    loadHotelImages(similarHotels)
                }
            } catch (e: Exception) {
                // Don't show cancellation errors to the user
                if (e !is kotlinx.coroutines.CancellationException) {
                    _state.value = _state.value.copy(
                        isLoadingRelated = false,
                        errorMessage = "Failed to load similar hotels: ${e.message}"
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoadingRelated = false
                    )
                }
            }
        }

        // Automatically set a timeout to stop loading if it takes too long
        viewModelScope.launch {
            delay(45000) // 45 seconds
            if (_state.value.isLoadingRelated) {
                _state.value = _state.value.copy(
                    isLoadingRelated = false,
                    errorMessage = "Loading similar hotels took too long. Please try again."
                )
                currentJob.cancel()
            }
        }
    }

    private fun loadHotelImages(hotels: List<Hotel>) {
        viewModelScope.launch {
            for (hotel in hotels) {
                // Set null as a placeholder while loading
                _hotelImages.update { it + (hotel.hotelId to null) }

                try {
                    val image = hotelThumbnailRepository.getHotelThumbnail(hotel)
                    _hotelImages.update { it + (hotel.hotelId to image) }
                } catch (e: Exception) {
                    // Keep the null placeholder on error
                }
            }
        }
    }

    private fun loadExplanations(hotels: List<Hotel>) {
        viewModelScope.launch {
            for (hotel in hotels) {
                try {
                    val explanation = recommendationRepository.getRecommendationExplanation(hotel)
                    _explanations.update { it + (hotel.hotelId to explanation) }
                } catch (e: Exception) {
                    _explanations.update { it + (hotel.hotelId to "Recommended based on your preferences.") }
                }
            }
        }
    }

    fun navigateToHotelDetails(hotel: Hotel) {
        sharedViewModel.onSelectHotel(hotel)
        navController.navigate(HotelRoute.HotelDetail(hotel.hotelId))
    }
    
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

data class RecommendationState(
    val recommendations: List<Hotel> = emptyList(),
    val relatedHotels: List<Hotel> = emptyList(),
    val relatedHotelId: String? = null,
    val isLoading: Boolean = false,
    val isLoadingRelated: Boolean = false,
    val errorMessage: String? = null
)