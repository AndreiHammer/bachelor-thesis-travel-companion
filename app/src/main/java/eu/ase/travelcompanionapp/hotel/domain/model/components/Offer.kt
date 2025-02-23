package eu.ase.travelcompanionapp.hotel.domain.model.components

data class Offer(
    val id: String?,
    val checkInDate: String?,
    val checkOutDate: String?,
    val rateCode: String?,
    val description: String?,
    val boardType: String?,
    val room: Room?,
    val guests: Guests?,
    val price: Price?,
    val policies: Policies?
)

data class Room(
    val type: String?,
    val name: String?,
    val typeEstimated: TypeEstimated?,
    val description: String?
)

data class TypeEstimated(
    val category: String?,
    val beds: Int?,
    val bedType: String?
)

data class Guests(
    val adults: Int?
)

data class Price(
    val currency: String?,
    val base: String?,
    val total: String?,
    val taxes: List<Tax> = emptyList(),
    val variations: Variations?
)

data class Tax(
    val amount: String?,
    val currency: String?,
    val code: String?,
    val pricingFrequency: String?,
    val pricingMode: String?,
    val percentage: String?,
    val included: Boolean?
)

data class Variations(
    val average: Average?,
    val changes: List<Change> = emptyList()
)

data class Average(
    val base: String?
)

data class Change(
    val startDate: String?,
    val endDate: String?,
    val total: String?
)

data class Policies(
    val cancellations: List<Cancellation> = emptyList(),
    val deposit: Deposit?,
    val paymentType: String?
)

data class Cancellation(
    val type: String?,
    val description: String?
)

data class Deposit(
    val acceptedPayments: AcceptedPayments?
)

data class AcceptedPayments(
    val creditCards: List<String> = emptyList(),
    val methods: List<String> = emptyList()
)
