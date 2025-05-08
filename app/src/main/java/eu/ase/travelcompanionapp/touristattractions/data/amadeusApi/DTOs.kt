package eu.ase.travelcompanionapp.touristattractions.data.amadeusApi

import eu.ase.travelcompanionapp.hotel.data.amadeusApi.GeoCodeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TouristAttractionResponse(
    @SerialName("meta") var meta: MetaDto? = MetaDto(),
    @SerialName("data") var data: ArrayList<TouristAttractionDto> = arrayListOf()
)

@Serializable
data class TouristAttractionDto(
    @SerialName("id") var id: String? = null,
    @SerialName("type") var type: String? = null,
    @SerialName("self") var self: SelfDto? = SelfDto(),
    @SerialName("name") var name: String? = null,
    @SerialName("description") var description: String? = null,
    @SerialName("geoCode") var geoCode: GeoCodeDto? = GeoCodeDto(),
    @SerialName("rating") var rating : String? = null,
    @SerialName("pictures") var pictures: ArrayList<String> = arrayListOf(),
    @SerialName("bookingLink") var bookingLink: String? = null,
    @SerialName("price") var price: PriceDto? = PriceDto(),
)

@Serializable
data class PriceDto(
    @SerialName("currencyCode") var currency: String? = null,
    @SerialName("amount") var amount: String? = null,
)

@Serializable
data class SelfDto(
    @SerialName("href") var href: String? = null,
    @SerialName("methods") var methods: ArrayList<String> = arrayListOf()
)

@Serializable
data class MetaDto(
    @SerialName("count") var count: Int? = null,
    @SerialName("links") var links: LinksDto? = LinksDto()
)

@Serializable
data class LinksDto(
    @SerialName("self") var self: String? = null
)