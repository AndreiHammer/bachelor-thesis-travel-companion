package eu.ase.travelcompanionapp.booking.domain.models

data class PaymentIntentResponse(
    val clientSecret: String,
    val publishableKey: String
)
