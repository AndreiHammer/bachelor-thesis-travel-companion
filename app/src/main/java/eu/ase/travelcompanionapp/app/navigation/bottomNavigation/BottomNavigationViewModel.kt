package eu.ase.travelcompanionapp.app.navigation.bottomNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.core.domain.utils.FavoriteEvent
import eu.ase.travelcompanionapp.core.domain.utils.FavoritesEventBus
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BottomNavigationViewModel(
    private val favouriteHotelRepository: FavouriteHotelRepository
) : ViewModel() {
    private val _favouriteCount = MutableStateFlow(0)
    val favouriteCount: StateFlow<Int> = _favouriteCount

    init {
        loadFavouriteCount()
        listenForFavoriteEvents()
    }

    private fun listenForFavoriteEvents() {
        viewModelScope.launch {
            FavoritesEventBus.events.collect { event ->
                when (event) {
                    is FavoriteEvent.CountChanged -> loadFavouriteCount()
                }
            }
        }
    }

    fun loadFavouriteCount() {
        viewModelScope.launch {
            favouriteHotelRepository.getFavouriteHotels().collectLatest { hotels ->
                _favouriteCount.value = hotels.size
            }
        }
    }
}