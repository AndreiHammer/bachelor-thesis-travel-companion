package eu.ase.travelcompanionapp.booking.presentation.bookinghistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BookingHistoryViewModel(
    private val bookingRecordRepository: BookingRecordRepository,
    private val navController: NavController
) : ViewModel() {
    
    private val _state = MutableStateFlow(BookingHistoryState())
    val state: StateFlow<BookingHistoryState> = _state.asStateFlow()
    
    init {
        loadBookingHistory()
    }
    
    private fun loadBookingHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            bookingRecordRepository.getUserBookings()
                .catch { e -> 
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load booking history"
                    )
                }
                .collect { bookings ->
                    _state.value = _state.value.copy(
                        bookings = bookings,
                        isLoading = false,
                        error = if (bookings.isEmpty()) "No bookings found" else null
                    )
                }
        }
    }
    
    fun onBackClick() {
        navController.popBackStack()
    }
    
    fun onBookingClick(booking: BookingInfo) {
        if (booking.hotelId?.isNotEmpty() == true) {
            navController.navigate(HotelRoute.HotelDetail(booking.hotelId))
        }
    }
    
    data class BookingHistoryState(
        val bookings: List<BookingInfo> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
} 