package eu.ase.travelcompanionapp.touristattractions.domain.model

data class TouristAttraction(
    var id: String? = null,
    var type: String? = null,
    var self: Self? = null,
    var name: String? = null,
    var description: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var rating: String? = null,
    var pictures: List<String> = emptyList(),
    var bookingLink: String? = null,
    var price: Price? = null,
    var originalPrice: Price? = null,
)

data class Price(
    var amount: String? = null,
    var currencyCode: String? = null
)

data class Self(
    var href : String? = null,
    var methods : ArrayList<String> = arrayListOf()
)

