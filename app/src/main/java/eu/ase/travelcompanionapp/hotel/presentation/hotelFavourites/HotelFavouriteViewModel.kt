package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.Bitmap
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.core.domain.utils.FavoriteEvent
import eu.ase.travelcompanionapp.core.domain.utils.FavoritesEventBus
import eu.ase.travelcompanionapp.hotel.domain.model.HotelWithBookingDetails
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.model.HotelPrice
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.user.domain.service.PriceConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HotelFavouriteViewModel(
    private val navController: NavController,
    private val sharedViewModel: SharedViewModel,
    private val favouriteHotelRepository: FavouriteHotelRepository,
    private val hotelRepository: HotelRepositoryAmadeusApi,
    private val priceConverter: PriceConverter,
    private val hotelThumbnailRepository: HotelThumbnailRepository
) : ViewModel() {
    private val _hotelState = MutableStateFlow(HotelFavouriteState())
    val hotelState = _hotelState.asStateFlow()

    private val _hotelPrices = MutableStateFlow<Map<String, HotelPrice>>(emptyMap())
    val hotelPrices = _hotelPrices.asStateFlow()

    private val _hotelImages = MutableStateFlow<Map<String, Bitmap?>>(emptyMap())
    val hotelImages: StateFlow<Map<String, Bitmap?>> = _hotelImages

    fun loadHotelImages() {
        viewModelScope.launch {
            val currentHotels = _hotelState.value.hotelsWithDetails

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

    fun getHotelFavourites() {
        viewModelScope.launch {
            _hotelState.value = _hotelState.value.copy(isLoading = true)

            favouriteHotelRepository.getFavouriteHotels().collectLatest { hotels ->
                val hotelsWithDetails = mutableListOf<HotelWithBookingDetails>()

                for (hotel in hotels) {
                    val bookingDetails = favouriteHotelRepository.getBookingDetails(hotel.hotelId)
                    hotelsWithDetails.add(HotelWithBookingDetails(hotel, bookingDetails))
                }

                _hotelState.value = _hotelState.value.copy(
                    hotelsWithDetails = hotelsWithDetails,
                    isLoading = false,
                    errorMessage = if (hotels.isEmpty()) "No favorite hotels found yet" else null
                )

                fetchPricesForFavourites(hotelsWithDetails)
            }
        }
    }
    
    private fun fetchPricesForFavourites(hotelsWithDetails: List<HotelWithBookingDetails>) {
        viewModelScope.launch {
            val batchSize = 5
            
            hotelsWithDetails.chunked(batchSize).forEachIndexed { batchIndex, hotelBatch ->
                if (batchIndex > 0) {
                    delay(1000)
                }
                
                for (hotelWithDetails in hotelBatch) {
                    val hotel = hotelWithDetails.hotel
                    val bookingDetails = hotelWithDetails.bookingDetails
                    
                    if (bookingDetails != null && 
                        bookingDetails.checkInDate.isNotEmpty() && 
                        bookingDetails.checkOutDate.isNotEmpty() && 
                        bookingDetails.adults > 0) {
                        
                        _hotelPrices.update { currentPrices ->
                            currentPrices + (hotel.hotelId to HotelPrice(isLoading = true))
                        }
                        
                        try {
                            val dateUtils = DateUtils()
                            val apiCheckInDate = dateUtils.displayDateToApiFormat(bookingDetails.checkInDate)
                            val apiCheckOutDate = dateUtils.displayDateToApiFormat(bookingDetails.checkOutDate)

                            hotelRepository.searchHotelOffers(
                                hotelIds = hotel.hotelId,
                                checkInDate = apiCheckInDate,
                                checkOutDate = apiCheckOutDate,
                                adults = bookingDetails.adults.toString(),
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
                        } catch (e: Exception) {
                            _hotelPrices.update { currentPrices ->
                                currentPrices + (hotel.hotelId to HotelPrice(
                                    isLoading = false,
                                    hasError = true
                                ))
                            }
                        }
                    }
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

    private fun removeFavourite(hotelId: String) {
        viewModelScope.launch {
            favouriteHotelRepository.removeFavourite(hotelId)

            FavoritesEventBus.emitEvent(FavoriteEvent.CountChanged)

            getHotelFavourites()
        }
    }

    fun handleAction(action: HotelFavouriteAction) {
        when (action) {
            is HotelFavouriteAction.OnHotelClick -> {
                viewModelScope.launch {
                    sharedViewModel.onSelectHotel(action.hotel)

                    val bookingDetails = favouriteHotelRepository.getBookingDetails(action.hotel.hotelId)
                    if (bookingDetails != null) {
                        sharedViewModel.updateBookingDetailsFromFavourite(
                            bookingDetails.checkInDate,
                            bookingDetails.checkOutDate,
                            bookingDetails.adults
                        )
                    }

                    navController.navigate(
                        HotelRoute.HotelDetail(action.hotel.hotelId)
                    )
                }
            }
            is HotelFavouriteAction.OnBackClick -> {
                navController.popBackStack()
            }
            is HotelFavouriteAction.OnRemoveFavourite -> {
                removeFavourite(action.hotelId)
            }
        }
    }

    data class HotelFavouriteState(
        val hotelsWithDetails: List<HotelWithBookingDetails> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

}

