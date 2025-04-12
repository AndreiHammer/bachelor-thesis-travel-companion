package eu.ase.travelcompanionapp.payment.data

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentRequest
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentResponse

interface PaymentService {
    suspend fun createPaymentIntent(request: PaymentIntentRequest): Result<PaymentIntentResponse, DataError>
}