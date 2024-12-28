package eu.ase.travelcompanionapp.hotel.domain.model


data class Hotel(
    val hotelId: String,
    val chainCode: String?,
    val iataCode: String,
    val dupeId: Int?,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val countryCode : String,
    val amenities: ArrayList<String> = arrayListOf(),
    val rating: Int?,
    val giataId: Int?,
    val phone: String?
)
