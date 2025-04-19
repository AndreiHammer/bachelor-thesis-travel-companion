package eu.ase.travelcompanionapp.booking.data.payment

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.booking.domain.models.PaymentIntentRequest
import eu.ase.travelcompanionapp.booking.domain.models.PaymentIntentResponse

interface PaymentService {
    suspend fun createPaymentIntent(request: PaymentIntentRequest): Result<PaymentIntentResponse, DataError>
}