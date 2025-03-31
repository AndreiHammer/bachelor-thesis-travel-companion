package eu.ase.travelcompanionapp.hotel.presentation.hotelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.hotel.domain.model.BookingDetails
import eu.ase.travelcompanionapp.hotel.domain.model.HotelWithBookingDetails
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.model.HotelPrice
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.user.domain.service.PriceConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HotelListViewModel(
    private val hotelRepository: HotelRepositoryAmadeusApi,
    private val cityToIATACodeRepository: CityToIATACodeRepository,
    private val navController: NavHostController,
    private val sharedViewModel: SharedViewModel,
    private val priceConverter: PriceConverter,
    private val hotelThumbnailRepository: HotelThumbnailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HotelListState())
    val hotelState: StateFlow<HotelListState> get() = _state
    
    private val _hotelPrices = MutableStateFlow<Map<String, HotelPrice>>(emptyMap())
    val hotelPrices: StateFlow<Map<String, HotelPrice>> = _hotelPrices

    private val _hotelImages = MutableStateFlow<Map<String, Bitmap?>>(emptyMap())
    val hotelImages: StateFlow<Map<String, Bitmap?>> = _hotelImages

    fun loadHotelImages() {
        viewModelScope.launch {
            val currentHotels = _state.value.hotelItems

            for (hotelItem in currentHotels) {
                val hotel = hotelItem.hotel
                _hotelImages.update { it + (hotel.hotelId to null) }

                try {
                    val image = hotelThumbnailRepository.getHotelThumbnail(hotel)
                    _hotelImages.update { it + (hotel.hotelId to image) }
                } catch (e: Exception) {
                    // Keep the placeholder on error (null value)
                }
            }
        }
    }

    fun getHotelListByCity(city: String, amenities: String, rating: String) {
        
        val iataCode = cityToIATACodeRepository.getIATACode(city)
        if (iataCode == null) {
            _state.value = HotelListState(
                hotelItems = emptyList(),
                errorMessage = "Invalid city code",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = HotelListState(
                hotelItems = emptyList(),
                errorMessage = null,
                isLoading = true
            )
            
            hotelRepository.searchHotelsByCity(iataCode, amenities, rating) { result ->
                when (result) {
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            DataError.Remote.NOT_FOUND -> "No hotels found in $city"
                            DataError.Remote.UNKNOWN -> "City not found"
                            else -> "Error fetching hotels"
                        }
                        _state.value = HotelListState(
                            hotelItems = emptyList(),
                            errorMessage = errorMessage,
                            isLoading = false
                        )
                    }
                    is Result.Success -> {
                        val checkInDate = sharedViewModel.selectedCheckInDate.value
                        val checkOutDate = sharedViewModel.selectedCheckOutDate.value
                        val adults = sharedViewModel.selectedAdults.value
                        
                        val bookingDetails = if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && adults > 0) {
                            BookingDetails(checkInDate, checkOutDate, adults)
                        } else null
                        
                        val hotelItems = result.data.map { hotel ->
                            HotelWithBookingDetails(hotel, bookingDetails)
                        }
                        
                        _state.value = HotelListState(
                            hotelItems = hotelItems,
                            errorMessage = if (result.data.isEmpty()) "No hotels found in $city" else null,
                            isLoading = false
                        )
                        
                        fetchHotelPrices(hotelItems)
                    }
                }
            }
        }
    }

    fun getHotelListByLocation(latitude: Double, longitude: Double, radius: Int, amenities: String, rating: String) {
        viewModelScope.launch {
            _state.value = HotelListState(
                hotelItems = emptyList(),
                errorMessage = null,
                isLoading = true
            )
            
            hotelRepository.searchHotelsByLocation(
                latitude,
                longitude,
                radius,
                amenities,
                rating
            ) { result ->
                when (result) {
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            DataError.Remote.NOT_FOUND -> "No hotels found in this area"
                            DataError.Remote.UNKNOWN -> "Invalid area"
                            else -> "Error fetching hotels"
                        }
                        _state.value = HotelListState(
                            hotelItems = emptyList(),
                            errorMessage = errorMessage,
                            isLoading = false
                        )
                    }
                    is Result.Success -> {
                        val checkInDate = sharedViewModel.selectedCheckInDate.value
                        val checkOutDate = sharedViewModel.selectedCheckOutDate.value
                        val adults = sharedViewModel.selectedAdults.value
                        
                        val bookingDetails = if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && adults > 0) {
                            BookingDetails(checkInDate, checkOutDate, adults)
                        } else null
                        
                        val hotelItems = result.data.map { hotel ->
                            HotelWithBookingDetails(hotel, bookingDetails)
                        }
                        
                        _state.value = HotelListState(
                            hotelItems = hotelItems,
                            errorMessage = if (result.data.isEmpty()) "No hotels found in this area" else null,
                            isLoading = false
                        )
                        
                        fetchHotelPrices(hotelItems)
                    }
                }
            }
        }
    }
    
    private fun fetchHotelPrices(hotelItems: List<HotelWithBookingDetails>) {
        if (hotelItems.isEmpty() || hotelItems.first().bookingDetails == null) {
            return
        }
        
        val bookingDetails = hotelItems.first().bookingDetails!!
        val checkInDate = bookingDetails.checkInDate
        val checkOutDate = bookingDetails.checkOutDate
        val adults = bookingDetails.adults

        if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && adults > 0) {
            viewModelScope.launch {
                try {
                    val dateUtils = DateUtils()
                    val apiCheckInDate = dateUtils.displayDateToApiFormat(checkInDate)
                    val apiCheckOutDate = dateUtils.displayDateToApiFormat(checkOutDate)

                    val batchSize = 5
                    hotelItems.chunked(batchSize).forEachIndexed { batchIndex, hotelBatch ->
                        if (batchIndex > 0) {
                           delay(1000)
                        }

                        hotelBatch.forEach { hotelItem ->
                            val hotel = hotelItem.hotel
                            _hotelPrices.update { currentPrices ->
                                currentPrices + (hotel.hotelId to HotelPrice(isLoading = true))
                            }
                            
                            hotelRepository.searchHotelOffers(
                                hotelIds = hotel.hotelId,
                                checkInDate = apiCheckInDate,
                                checkOutDate = apiCheckOutDate,
                                adults = adults.toString(),
                                bestRateOnly = true
                            ) { result ->
                                when (result) {
                                    is Result.Error -> {
                                        _hotelPrices.update { currentPrices ->
                                            currentPrices + (hotel.hotelId to HotelPrice(
                                                isLoading = false,
                                                hasError = true
                                            ))
                                        }
                                    }
                                    is Result.Success -> {
                                        handleBestOfferResult(result.data, hotel.hotelId)
                                    }
                                }
                            }
                            delay(200)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    

    private fun handleBestOfferResult(offers: List<HotelOffer>, hotelId: String) {
        if (offers.isEmpty()) {
            _hotelPrices.update { currentPrices ->
                currentPrices + (hotelId to HotelPrice(
                    isLoading = false,
                    hasError = false,
                    noOffers = true
                ))
            }
            return
        }

        val hotelOffer = offers.firstOrNull()
        if (hotelOffer == null || hotelOffer.offers.isEmpty()) {
            _hotelPrices.update { currentPrices ->
                currentPrices + (hotelId to HotelPrice(
                    isLoading = false,
                    hasError = false,
                    noOffers = true
                ))
            }
            return
        }

        val bestOffer = hotelOffer.offers.first()
        val totalPrice = bestOffer.price?.total?.toDoubleOrNull()
        val currency = bestOffer.price?.currency
        
        if (totalPrice != null && currency != null) {
            viewModelScope.launch {
                priceConverter.convertPrice(totalPrice, currency) { result ->
                    when (result) {
                        is Result.Error -> {
                            _hotelPrices.update { currentPrices ->
                                currentPrices + (hotelId to HotelPrice(
                                    isLoading = false,
                                    hasError = false,
                                    originalPrice = totalPrice,
                                    originalCurrency = currency
                                ))
                            }
                        }
                        is Result.Success -> {
                            _hotelPrices.update { currentPrices ->
                                currentPrices + (hotelId to HotelPrice(
                                    isLoading = false,
                                    hasError = false,
                                    originalPrice = totalPrice,
                                    originalCurrency = currency,
                                    convertedPrice = result.data
                                ))
                            }
                        }
                    }
                }
            }
        } else {
            _hotelPrices.update { currentPrices ->
                currentPrices + (hotelId to HotelPrice(
                    isLoading = false,
                    hasError = false,
                    noOffers = true
                ))
            }
        }
    }

    fun handleAction(action: HotelListAction) {
        when (action) {
            is HotelListAction.OnHotelClick -> {
                sharedViewModel.onSelectHotel(action.hotel)
                navController.navigate(
                    HotelRoute.HotelDetail(action.hotel.hotelId)
                )
            }

            HotelListAction.OnBackClick -> {
                navController.popBackStack()
            }
        }
    }

    data class HotelListState(
        val hotelItems: List<HotelWithBookingDetails> = emptyList(),
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )
}
