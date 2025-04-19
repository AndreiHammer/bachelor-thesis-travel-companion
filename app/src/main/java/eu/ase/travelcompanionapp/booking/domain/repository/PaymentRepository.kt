package eu.ase.travelcompanionapp.booking.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.booking.domain.models.PaymentIntentResponse

interface PaymentRepository {
    suspend fun createPaymentIntent(
        amount: Long,
        currency: String,
        description: String,
        metadata: Map<String, String> = emptyMap()
    ): Result<PaymentIntentResponse, DataError>
}