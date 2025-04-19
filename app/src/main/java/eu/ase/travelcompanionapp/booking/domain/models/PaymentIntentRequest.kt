package eu.ase.travelcompanionapp.booking.domain.models

data class PaymentIntentRequest(
    val amount: Long,
    val currency: String,
    val description: String,
    val metadata: Map<String, String> = emptyMap()
)
