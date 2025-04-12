package eu.ase.travelcompanionapp.payment.data

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentRequest
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentResponse
import eu.ase.travelcompanionapp.payment.domain.repository.PaymentRepository

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