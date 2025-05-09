package eu.ase.travelcompanionapp.touristattractions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.TouristAttractionsRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TouristAttractionsViewModel(
    private val touristAttractionRepository: TouristAttractionRepositoryAmadeusApi,
    private val navController: NavController,
    private val sharedViewModel: TouristSharedViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(TouristAttractionsState())
    val state: StateFlow<TouristAttractionsState> = _state.asStateFlow()


    companion object {
        private val attractionsCache = mutableMapOf<String, TouristAttraction>()

        fun cacheAttraction(attraction: TouristAttraction) {
            attraction.id?.let { id ->
                attractionsCache[id] = attraction
            }
        }
    }

    fun loadAttractionsByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            touristAttractionRepository.searchTouristAttractionsByLocation(latitude, longitude) { result ->
                when (result) {
                    is Result.Error -> {
                        _state.update { it.copy(
                            isLoading = false,
                            error = "Failed to load tourist attractions: ${result.error}",
                            attractions = emptyList()
                        )}
                    }
                    is Result.Success -> {
                        val attractions = result.data

                        attractions.forEach { attraction ->
                            attraction.id?.let { id ->
                                attractionsCache[id] = attraction
                            }
                        }

                        _state.update { it.copy(
                            isLoading = false,
                            attractions = attractions,
                            error = null
                        )}
                    }
                }
            }
        }
    }

    fun getAttractionById(id: String): TouristAttraction? {
        val fromState = state.value.attractions.find { it.id == id }
        if (fromState != null) return fromState
        
        val fromSharedViewModel = sharedViewModel.selectedAttraction.value
        if (fromSharedViewModel?.id == id) return fromSharedViewModel

        return attractionsCache[id]
    }

    fun navigateToAttractionDetails(attraction: TouristAttraction) {
        attraction.id?.let { attractionId ->
            cacheAttraction(attraction)
            sharedViewModel.setSelectedAttraction(attraction)
            sharedViewModel.setSelectedAttractionId(attractionId)
            navController.navigate(
                TouristAttractionsRoute.TouristAttractionDetails
            )
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    data class TouristAttractionsState(
        val isLoading: Boolean = false,
        val attractions: List<TouristAttraction> = emptyList(),
        val error: String? = null
    )
} 