package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.app.navigation.routes.PaymentRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.booking.domain.repository.BookingService
import eu.ase.travelcompanionapp.user.domain.model.Currency
import eu.ase.travelcompanionapp.user.domain.service.PriceConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HotelOffersViewModel(
    private val hotelRepositoryAmadeusApi: HotelRepositoryAmadeusApi,
    private val priceConverter: PriceConverter,
    private val navController: NavHostController,
    private val bookingService: BookingService
) : ViewModel() {

    private val _state = MutableStateFlow(HotelOffersState())
    val state: StateFlow<HotelOffersState> = _state

    private val _convertedPrices = MutableStateFlow<Map<String, Currency>>(emptyMap())
    val convertedPrices = _convertedPrices.asStateFlow()

    private val _currentBooking = MutableStateFlow<BookingInfo?>(null)

    fun getHotelOffers(hotelId: String, checkInDate: String, checkOutDate: String, adults: Int) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val dateUtils = DateUtils()
                val apiCheckInDate = dateUtils.displayDateToApiFormat(checkInDate)
                val apiCheckOutDate = dateUtils.displayDateToApiFormat(checkOutDate)

                hotelRepositoryAmadeusApi.searchHotelOffers(
                    hotelId,
                    apiCheckInDate,
                    apiCheckOutDate,
                    adults.toString()
                ) { result ->
                    when (result) {
                        is Result.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = result.error.name
                            )
                        }
                        is Result.Success -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                offers = result.data
                            )

                            convertOfferPrices()
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun convertOfferPrices() {
        viewModelScope.launch {
            _state.value.offers.forEach { hotelOffer ->
                hotelOffer.offers.forEach { offer ->
                    offer.price?.total?.toDoubleOrNull()?.let { priceAmount ->
                        val currencyCode = offer.price.currency ?: "EUR"

                        priceConverter.convertPrice(priceAmount, currencyCode) { result ->
                            when (result) {
                                is Result.Error -> {
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        error = result.error.name
                                    )
                                }
                                is Result.Success -> {
                                    _convertedPrices.update { currentMap ->
                                        currentMap + (offer.id.orEmpty() to result.data)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun handleAction(action: HotelOffersAction) {
        when (action) {
            HotelOffersAction.OnBackClick -> {
                navController.popBackStack()
            }
            is HotelOffersAction.OnBookNow -> {
                viewModelScope.launch {
                    val bookingDetails = bookingService.startBooking(action.offer)
                    _currentBooking.value = bookingDetails

                    navController.navigate(PaymentRoute.Payment)
                }
            }
        }
    }

    data class HotelOffersState(
        val isLoading: Boolean = false,
        val offers: List<HotelOffer> = emptyList(),
        val error: String? = null
    )
}