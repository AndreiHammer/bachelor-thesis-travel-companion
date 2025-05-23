package eu.ase.travelcompanionapp.booking.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.paymentsheet.PaymentSheetResult
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.utils.BookingEvent
import eu.ase.travelcompanionapp.core.domain.utils.EventBus
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import eu.ase.travelcompanionapp.booking.domain.repository.BookingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val bookingService: BookingService,
    private val bookingRecordRepository: BookingRecordRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Ready)
    val paymentState: StateFlow<PaymentState> = _paymentState

    private val _currentBooking = MutableStateFlow<BookingInfo?>(null)
    val currentBooking: StateFlow<BookingInfo?> = _currentBooking
    
    private var currentClientSecret: String? = null

    init {
        viewModelScope.launch {
            bookingService.currentBooking.collectLatest { booking ->
                if (booking != null) {
                    _currentBooking.value = booking
                }
            }
        }
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
                        currentClientSecret = result.data.clientSecret
                        
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
                saveBookingRecord()
            }
            is PaymentSheetResult.Canceled -> {
                _paymentState.value = PaymentState.Ready
            }
            is PaymentSheetResult.Failed -> {
                _paymentState.value = PaymentState.Error(result.error.message ?: "Unknown error")
            }
        }
    }
    
    private fun saveBookingRecord() {
        viewModelScope.launch {
            val booking = _currentBooking.value
            if (booking == null) {
                _paymentState.value = PaymentState.Error("No booking details found")
                return@launch
            }

            val paymentId = currentClientSecret?.split("_secret_")?.firstOrNull()
            
            try {
                when (bookingRecordRepository.saveBookingRecord(booking, paymentId ?: "")) {
                    is Result.Success -> {
                        val userEmail = authRepository.getCurrentUserEmail() ?: ""

                        if (userEmail.isNotEmpty()) {
                            bookingRecordRepository.sendBookingConfirmationEmail(booking, userEmail)
                        }
                        
                        _paymentState.value = PaymentState.Success
                        EventBus.bookings.emitEvent(BookingEvent.CountChanged)
                    }
                    is Result.Error -> {
                        _paymentState.value = PaymentState.Success
                    }
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Success
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