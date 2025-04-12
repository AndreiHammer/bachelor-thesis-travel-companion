package eu.ase.travelcompanionapp.payment.data

import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import eu.ase.travelcompanionapp.BuildConfig
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentRequest
import eu.ase.travelcompanionapp.payment.domain.models.PaymentIntentResponse
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebasePaymentService: PaymentService {
    private val functions = Firebase.functions

    override suspend fun createPaymentIntent(request: PaymentIntentRequest): Result<PaymentIntentResponse, DataError> {
        return try {

            val data = mapOf(
                "amount" to request.amount,
                "currency" to request.currency,
                "description" to request.description,
                "metadata" to request.metadata
            ).toMap()

            val result = withContext(Dispatchers.IO) {
                functions
                    .getHttpsCallable("createPaymentIntent")
                    .call(data)
                    .await()
            }

            val resultData = result.data as HashMap<*, *>


            val clientSecret = resultData["clientSecret"] as String?
                ?: return Result.Error(DataError.Remote.SERVER)

            val publishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY

            val response = PaymentIntentResponse(
                clientSecret = clientSecret,
                publishableKey = publishableKey
            )

            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }
}