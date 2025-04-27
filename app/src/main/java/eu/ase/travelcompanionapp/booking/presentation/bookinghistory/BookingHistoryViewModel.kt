package eu.ase.travelcompanionapp.booking.presentation.bookinghistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.CrossGraphDataHolder
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class BookingHistoryViewModel(
    private val bookingRecordRepository: BookingRecordRepository,
    private val navController: NavController,
    private val hotelRepository: HotelRepositoryAmadeusApi,
    private val sharedViewModel: SharedViewModel? = null
) : ViewModel() {

    private val _state = MutableStateFlow(BookingHistoryState())
    val state: StateFlow<BookingHistoryState> = _state.asStateFlow()
    
    private val dateUtils = DateUtils()
    private val _cachedHotels = MutableStateFlow<Map<String, Hotel>>(emptyMap())
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

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
                    val (active, past) = categorizeBookings(bookings)
                    
                    _state.value = _state.value.copy(
                        bookings = bookings,
                        activeBookings = active,
                        pastBookings = past,
                        isLoading = false,
                        error = if (bookings.isEmpty()) "No bookings found" else null
                    )
                    
                    preloadHotelData(bookings)
                }
        }
    }
    
    private fun categorizeBookings(bookings: List<BookingInfo>): Pair<List<BookingInfo>, List<BookingInfo>> {
        val today = LocalDate.now()
        val active = mutableListOf<BookingInfo>()
        val past = mutableListOf<BookingInfo>()
        
        bookings.forEach { booking ->
            try {
                val checkInDate = parseBookingDate(booking.checkInDate)
                
                if (checkInDate != null && checkInDate.isAfter(today) || checkInDate == today) {
                    active.add(booking)
                } else {
                    past.add(booking)
                }
            } catch (e: Exception) {
                active.add(booking)
            }
        }
        
        return Pair(active, past)
    }
    
    private fun parseBookingDate(dateString: String?): LocalDate? {
        if (dateString.isNullOrEmpty()) return null
        
        return try {
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: DateTimeParseException) {
            try {
                dateUtils.parseDisplayDate(dateString)?.toLocalDate()
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun preloadHotelData(bookings: List<BookingInfo>) {
        viewModelScope.launch {
            val hotelIds = bookings.mapNotNull { it.hotelId }.distinct()
            
            hotelIds.forEach { hotelId ->
                val cachedHotel = _cachedHotels.value[hotelId]
                if (cachedHotel == null || needsValidCoordinates(cachedHotel)) {
                    fetchHotelData(hotelId)
                }
            }
        }
    }
    
    private fun needsValidCoordinates(hotel: Hotel): Boolean {
        return hotel.latitude == 0.0 || hotel.longitude == 0.0 || hotel.countryCode.isEmpty()
    }

    fun handleAction(action: BookingHistoryAction) {
        when(action) {
            is BookingHistoryAction.OnBookingClick -> onBookingClick(action.booking, action.hotel)
            is BookingHistoryAction.OnBackClick -> onBackClick()
            is BookingHistoryAction.OnTabSelected -> onTabSelected(action.index)
        }
    }
    
    private fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTabIndex = index)
    }

    private fun onBackClick() {
        navController.popBackStack()
    }

    private fun onBookingClick(booking: BookingInfo, hotel: Hotel) {
        viewModelScope.launch {
            if (booking.hotelId?.isNotEmpty() == true) {
                val currentDates = dateUtils.getCurrentAndNextDayDates()
                
                if (sharedViewModel != null) {
                    sharedViewModel.onSelectHotel(hotel)
                    sharedViewModel.updateBookingDetailsFromFavourite(
                        currentDates.first,
                        currentDates.second,
                        2
                    )
                }

                CrossGraphDataHolder.tempHotel = hotel
                CrossGraphDataHolder.tempCheckInDate = currentDates.first
                CrossGraphDataHolder.tempCheckOutDate = currentDates.second
                CrossGraphDataHolder.tempAdults = 2

                navController.navigate(HotelRoute.HotelDetail(booking.hotelId))
            }
        }
    }

    fun createHotelFromBooking(booking: BookingInfo): Hotel {
        val hotelId = booking.hotelId ?: return createFallbackHotel(null, booking.hotelName)
        
        val cachedHotel = _cachedHotels.value[hotelId]
        if (cachedHotel != null) {
            return cachedHotel
        }
        
        fetchHotelData(hotelId)
        return createFallbackHotel(hotelId, booking.hotelName)
    }
    
    private fun fetchHotelData(hotelId: String) {
        if (_cachedHotels.value.containsKey(hotelId)) return
        
        viewModelScope.launch {
            try {
                val currentDates = dateUtils.getCurrentAndNextDayDates()
                val checkInDate = dateUtils.displayDateToApiFormat(currentDates.first)
                val checkOutDate = dateUtils.displayDateToApiFormat(currentDates.second)
                
                hotelRepository.searchHotelOffers(
                    hotelIds = hotelId,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = "2",
                    bestRateOnly = true
                ) { result ->
                    when (result) {
                        is Result.Success -> {
                            result.data.firstOrNull()?.hotel?.let { hotel ->
                                val validHotel = if (needsValidCoordinates(hotel)) {
                                    hotel.copy(
                                        latitude = 44.4268,
                                        longitude = 26.1025,
                                        countryCode = hotel.countryCode.ifEmpty { "RO" }
                                    )
                                } else {
                                    hotel
                                }
                                
                                _cachedHotels.value += (hotelId to validHotel)
                                refreshUI(hotelId)
                            }
                        }
                        else -> {}
                    }
                }
            } catch (_: Exception) {}
        }
    }
    
    private fun refreshUI(hotelId: String) {
        if (_state.value.bookings.any { it.hotelId == hotelId }) {
            _state.value = _state.value.copy()
        }
    }
    
    private fun createFallbackHotel(hotelId: String?, hotelName: String? = null): Hotel {
        return Hotel(
            hotelId = hotelId ?: "",
            name = hotelName ?: "Unknown Hotel",
            chainCode = null,
            iataCode = "",
            dupeId = null,
            latitude = 44.4268,
            longitude = 26.1025,
            countryCode = "",
            amenities = arrayListOf(),
            rating = null,
            giataId = null,
            phone = null
        )
    }
    
    data class BookingHistoryState(
        val bookings: List<BookingInfo> = emptyList(),
        val activeBookings: List<BookingInfo> = emptyList(),
        val pastBookings: List<BookingInfo> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedTabIndex: Int = 0
    )
}

