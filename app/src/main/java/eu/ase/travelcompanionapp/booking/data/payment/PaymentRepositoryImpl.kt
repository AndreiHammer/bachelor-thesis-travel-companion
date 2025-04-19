package eu.ase.travelcompanionapp.booking.data.payment

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.booking.domain.models.PaymentIntentRequest
import eu.ase.travelcompanionapp.booking.domain.models.PaymentIntentResponse
import eu.ase.travelcompanionapp.booking.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val paymentService: PaymentService
) : PaymentRepository {

    override suspend fun createPaymentIntent(
        amount: Long,
        currency: String,
        description: String,
        metadata: Map<String, String>
    ): Result<PaymentIntentResponse, DataError> {
        val request = PaymentIntentRequest(amount, currency, description, metadata)
        return paymentService.createPaymentIntent(request)
    }
}