package eu.ase.travelcompanionapp.payment.presentation

import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.paymentsheet.PaymentSheetResult
import eu.ase.travelcompanionapp.payment.domain.models.BookingInfo
import eu.ase.travelcompanionapp.payment.domain.repository.BookingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val bookingService: BookingService
) : ViewModel() {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Ready)
    val paymentState: StateFlow<PaymentState> = _paymentState

    private val _currentBooking = MutableStateFlow<BookingInfo?>(null)
    val currentBooking: StateFlow<BookingInfo?> = _currentBooking

    init {
        viewModelScope.launch {
            bookingService.currentBooking.collectLatest { booking ->
                if (booking != null) {
                    _currentBooking.value = booking
                }
            }
        }
    }

    fun loadBookingDetails(bookingReference: String) {
        val existingBooking = bookingService.currentBooking.value
        
        if (existingBooking != null && existingBooking.bookingReference == bookingReference) {
            _currentBooking.value = existingBooking
        }

        _paymentState.value = PaymentState.Ready
    }

    fun startPayment() {
            viewModelScope.launch {
                _paymentState.value = PaymentState.Loading

                try {
                    val booking = _currentBooking.value
                    if (booking == null) {
                        _paymentState.value = PaymentState.Error("No active booking found")
                        return@launch
                    }
                    when (val result = bookingService.processPayment(booking)) {
                        is Result.Success -> {
                            _paymentState.value = PaymentState.ClientSecretReceived(
                                clientSecret = result.data.clientSecret,
                                publishableKey = result.data.publishableKey
                            )
                        }
                        is Result.Error -> {
                            _paymentState.value = PaymentState.Error("Payment error: ${result.error}")
                        }
                    }
                } catch (e: Exception) {
                    _paymentState.value = PaymentState.Error(e.message ?: "Unknown error")
                }
            }
    }

    fun handlePaymentResult(result: PaymentSheetResult) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                _paymentState.value = PaymentState.Success
            }
            is PaymentSheetResult.Canceled -> {
                _paymentState.value = PaymentState.Ready
            }
            is PaymentSheetResult.Failed -> {
                _paymentState.value = PaymentState.Error(result.error.message ?: "Unknown error")
            }
        }
    }
}

sealed class PaymentState {
    data object Ready : PaymentState()
    data object Loading : PaymentState()
    data object Success : PaymentState()
    class Error(val message: String) : PaymentState()
    class ClientSecretReceived(
        val clientSecret: String?,
        val publishableKey: String?
    ) : PaymentState()
}