package eu.ase.travelcompanionapp.app.navigation.bottomNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.core.domain.utils.BookingEvent
import eu.ase.travelcompanionapp.core.domain.utils.EventBus
import eu.ase.travelcompanionapp.core.domain.utils.FavouriteEvent
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BottomNavigationViewModel(
    private val favouriteHotelRepository: FavouriteHotelRepository,
    private val bookingRecordRepository: BookingRecordRepository
) : ViewModel() {
    private val _favouriteCount = MutableStateFlow(0)
    val favouriteCount: StateFlow<Int> = _favouriteCount

    private val _bookingCount = MutableStateFlow(0)
    val bookingCount: StateFlow<Int> = _bookingCount.asStateFlow()

    init {
        loadFavouriteCount()
        loadBookingCount()
        listenForFavoriteEvents()
        listenForBookingEvents()
    }

    private fun listenForFavoriteEvents() {
        viewModelScope.launch {
            EventBus.favourites.events.collect { event ->
                when (event) {
                    is FavouriteEvent.CountChanged -> loadFavouriteCount()
                }
            }
        }
    }
    
    private fun listenForBookingEvents() {
        viewModelScope.launch {
            EventBus.bookings.events.collect { event ->
                when (event) {
                    is BookingEvent.CountChanged -> loadBookingCount()
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

    fun loadBookingCount() {
        viewModelScope.launch {
            bookingRecordRepository.getUserBookings().collectLatest { bookings ->
                _bookingCount.value = bookings.size
            }
        }
    }
}