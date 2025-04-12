package eu.ase.travelcompanionapp.payment.domain.models

data class PaymentIntentResponse(
    val clientSecret: String,
    val publishableKey: String
)
