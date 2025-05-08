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
    private val navController: NavController
) : ViewModel() {

    private val _state = MutableStateFlow(TouristAttractionsState())
    val state: StateFlow<TouristAttractionsState> = _state.asStateFlow()

    private val _selectedAttraction = MutableStateFlow<TouristAttraction?>(null)

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

        return attractionsCache[id]
    }

    fun navigateToAttractionDetails(attraction: TouristAttraction) {
        _selectedAttraction.value = attraction

        attraction.id?.let { attractionId ->
            cacheAttraction(attraction)
            navController.navigate(
                TouristAttractionsRoute.TouristAttractionDetails(attractionId)
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