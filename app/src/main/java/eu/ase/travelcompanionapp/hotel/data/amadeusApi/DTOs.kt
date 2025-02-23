package eu.ase.travelcompanionapp.hotel.data.amadeusApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AmadeusOAuth2TokenResponse(
    val type: String,
    val username: String,
    @SerialName("application_name")
    val applicationName: String,
    @SerialName("client_id")
    val clientId: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val state: String,
    val scope: String
)

@Serializable
data class HotelDto(
    @SerialName("chainCode") var chainCode: String? = null,
    @SerialName("iataCode") var iataCode: String? = null,
    @SerialName("dupeId") var dupeId: Int? = null,
    @SerialName("name") var name: String? = null,
    @SerialName("hotelId") var hotelId: String? = null,
    @SerialName("geoCode") var geoCode: GeoCodeDto? = GeoCodeDto(),
    @SerialName("address") var address: Address? = Address(),
    @SerialName("amenities") var amenities: ArrayList<String> = arrayListOf(),
    @SerialName("rating") var rating: Int? = null,
    @SerialName("giataId") var giataId: Int? = null,
    @SerialName("lastUpdate") var lastUpdate: String? = null,
    @SerialName("contact"   ) var contact   : Contact? = Contact()
)

@Serializable
data class Address(
    @SerialName("countryCode") var countryCode: String? = null
)

@Serializable
data class GeoCodeDto(
    @SerialName("latitude") var latitude: Double? = null,
    @SerialName("longitude") var longitude: Double? = null
)

@Serializable
data class HotelSearchResponse(
    @SerialName("data") var data: ArrayList<HotelDto> = arrayListOf(),
    @SerialName("errors") var errors: ArrayList<Errors> = arrayListOf()
)

@Serializable
data class Errors(
    @SerialName("code") var code: Int? = null,
    @SerialName("detail") var detail: String? = null,
    @SerialName("status") var status: Int? = null,
    @SerialName("title") var title: String? = null
)

@Serializable
data class Contact (
    @SerialName("phone" ) var phone : String? = null
)

@Serializable
data class HotelOffersResponse(
    @SerialName("data") var data: ArrayList<HotelOffersDto> = arrayListOf(),
)

@Serializable
data class HotelOffersDto (
    @SerialName("type"      ) var type      : String?           = null,
    @SerialName("hotel"     ) var hotel     : HotelDto?            = HotelDto(),
    @SerialName("available" ) var available : Boolean?          = null,
    @SerialName("offers"    ) var offers    : ArrayList<OffersDto> = arrayListOf(),
    @SerialName("self"      ) var self      : String?           = null
)

@Serializable
data class TypeEstimatedDto (
    @SerialName("category" ) var category : String? = null,
    @SerialName("beds"     ) var beds     : Int?    = null,
    @SerialName("bedType"  ) var bedType  : String? = null
)
@Serializable
data class DescriptionDto (
    @SerialName("text" ) var text : String? = null,
    @SerialName("lang" ) var lang : String? = null
)
@Serializable
data class RoomDto (
    @SerialName("type"          ) var type          : String?        = null,
    @SerialName("name"          ) var name          : String?        = null,
    @SerialName("typeEstimated" ) var typeEstimated : TypeEstimatedDto? = TypeEstimatedDto(),
    @SerialName("description"   ) var description   : DescriptionDto?   = DescriptionDto()
)
@Serializable
data class GuestsDto (
    @SerialName("adults" ) var adults : Int? = null
)
@Serializable
data class TaxesDto (
    @SerialName("amount"   ) var amount   : String? = null,
    @SerialName("currency" ) var currency : String? = null,
    @SerialName("code"   ) var code   : String? = null,
    @SerialName("pricingFrequency" ) var pricingFrequency : String? = null,
    @SerialName("pricingMode" ) var pricingMode : String? = null,
    @SerialName("percentage"   ) var percentage   : String? = null,
    @SerialName("included" ) var included : Boolean? = null
)
@Serializable
data class AverageDto (
    @SerialName("base" ) var base : String? = null
)
@Serializable
data class ChangesDto (
    @SerialName("startDate" ) var startDate : String? = null,
    @SerialName("endDate"   ) var endDate   : String? = null,
    @SerialName("total"     ) var total     : String? = null
)
@Serializable
data class VariationsDto (
    @SerialName("average" ) var average : AverageDto?           = AverageDto(),
    @SerialName("changes" ) var changes : ArrayList<ChangesDto> = arrayListOf()
)
@Serializable
data class PriceDto (
    @SerialName("currency"   ) var currency   : String?          = null,
    @SerialName("base"       ) var base       : String?          = null,
    @SerialName("total"      ) var total      : String?          = null,
    @SerialName("taxes"      ) var taxes      : ArrayList<TaxesDto> = arrayListOf(),
    @SerialName("variations" ) var variations : VariationsDto?      = VariationsDto()
)
@Serializable
data class CancellationsDto (
    @SerialName("type"        ) var type        : String?      = null,
    @SerialName("description" ) var description : DescriptionDto? = DescriptionDto()
)
@Serializable
data class AcceptedPaymentsDto (
    @SerialName("creditCards" ) var creditCards : ArrayList<String> = arrayListOf(),
    @SerialName("methods"     ) var methods     : ArrayList<String> = arrayListOf()
)
@Serializable
data class DepositDto (
    @SerialName("acceptedPayments" ) var acceptedPayments : AcceptedPaymentsDto? = AcceptedPaymentsDto()
)
@Serializable
data class PoliciesDto (
    @SerialName("cancellations" ) var cancellations : ArrayList<CancellationsDto> = arrayListOf(),
    @SerialName("deposit"       ) var deposit       : DepositDto?                 = DepositDto(),
    @SerialName("paymentType"   ) var paymentType   : String?                  = null
)
@Serializable
data class OffersDto (
    @SerialName("id"           ) var id           : String?      = null,
    @SerialName("checkInDate"  ) var checkInDate  : String?      = null,
    @SerialName("checkOutDate" ) var checkOutDate : String?      = null,
    @SerialName("rateCode"     ) var rateCode     : String?      = null,
    @SerialName("description"  ) var description  : DescriptionDto? = DescriptionDto(),
    @SerialName("boardType"    ) var boardType    : String?      = null,
    @SerialName("room"         ) var room         : RoomDto?        = RoomDto(),
    @SerialName("guests"       ) var guests       : GuestsDto?      = GuestsDto(),
    @SerialName("price"        ) var price        : PriceDto?       = PriceDto(),
    @SerialName("policies"     ) var policies     : PoliciesDto?    = PoliciesDto(),
    @SerialName("self"         ) var self         : String?      = null
)
@Serializable
data class BookingResponse (
    @SerialName("code"   ) var code   : Int?    = null,
    @SerialName("title"  ) var title  : String? = null,
    @SerialName("status" ) var status : Int?    = null,
    @SerialName("warnings" ) var warnings : ArrayList<Warnings> = arrayListOf(),
    @SerialName("data"     ) var data     : ArrayList<BookingData>     = arrayListOf(),
    @SerialName("errors") var errors: ArrayList<Errors> = arrayListOf()
)
@Serializable
data class Warnings (
    @SerialName("code"  ) var code  : Int?    = null,
    @SerialName("title" ) var title : String? = null
)
@Serializable
data class AssociatedRecords (
    @SerialName("reference"        ) var reference        : String? = null,
    @SerialName("originSystemCode" ) var originSystemCode : String? = null
)
@Serializable
data class BookingData (
    @SerialName("type"                   ) var type                   : String?                      = null,
    @SerialName("id"                     ) var id                     : String?                      = null,
    @SerialName("providerConfirmationId" ) var providerConfirmationId : String?                      = null,
    @SerialName("associatedRecords"      ) var associatedRecords      : ArrayList<AssociatedRecords> = arrayListOf()
)