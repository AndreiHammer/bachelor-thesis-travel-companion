package eu.ase.travelcompanionapp.touristattractions.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.TouristAttractionsRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi
import eu.ase.travelcompanionapp.user.domain.service.PriceConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TouristAttractionsViewModel(
    private val touristAttractionRepository: TouristAttractionRepositoryAmadeusApi,
    private val navController: NavController,
    private val sharedViewModel: TouristSharedViewModel,
    private val priceConverter: PriceConverter
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
                        convertAttractionPrices(attractions)
                    }
                }
            }
        }
    }
    
    @SuppressLint("DefaultLocale")
    private fun convertAttractionPrices(attractions: List<TouristAttraction>) {
        if (attractions.isEmpty()) {
            _state.update { it.copy(
                isLoading = false,
                attractions = emptyList(),
                error = null
            )}
            return
        }
        
        viewModelScope.launch {
            val convertedAttractions = mutableListOf<TouristAttraction>()
            var processedCount = 0
            
            fun updateStateIfAllProcessed() {
                if (++processedCount == attractions.size) {
                    _state.update { it.copy(
                        isLoading = false,
                        attractions = convertedAttractions,
                        error = null
                    )}
                }
            }
            
            attractions.forEach { attraction ->
                val price = attraction.price
                
                if (price?.amount == null || price.currencyCode == null) {
                    convertedAttractions.add(attraction)
                    updateStateIfAllProcessed()
                    return@forEach
                }
                
                try {
                    val amountDouble = price.amount!!.toDoubleOrNull()
                    
                    if (amountDouble == null) {
                        convertedAttractions.add(attraction)
                        updateStateIfAllProcessed()
                        return@forEach
                    }
                    
                    priceConverter.convertPrice(
                        price = amountDouble,
                        fromCurrency = price.currencyCode!!
                    ) { result ->
                        when (result) {
                            is Result.Success -> {
                                val convertedCurrency = result.data
                                val updatedAttraction = attraction.copy(
                                    price = attraction.price!!.copy(
                                        amount = String.format("%.2f", convertedCurrency.convertedAmount),
                                        currencyCode = convertedCurrency.code
                                    ),
                                    originalPrice = attraction.price
                                )
                                convertedAttractions.add(updatedAttraction)
                                
                                updatedAttraction.id?.let { id ->
                                    attractionsCache[id] = updatedAttraction
                                }
                            }
                            is Result.Error -> {
                                convertedAttractions.add(attraction)
                            }
                        }
                        updateStateIfAllProcessed()
                    }
                } catch (e: Exception) {
                    convertedAttractions.add(attraction)
                    updateStateIfAllProcessed()
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