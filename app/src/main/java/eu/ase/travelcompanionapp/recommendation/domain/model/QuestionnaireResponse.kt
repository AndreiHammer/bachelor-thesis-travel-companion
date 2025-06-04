package eu.ase.travelcompanionapp.recommendation.domain.model

data class QuestionnaireResponse(
    val budgetRange: String,
    val travelPurpose: String,
    val groupSize: String,
    val accommodationType: String,
    val locationPreference: String,
    val importanceFactors: ImportanceFactors,
    val importantAmenities: ArrayList<String> = arrayListOf(),
    val preferredContinents: ArrayList<String> = arrayListOf()
)

data class ImportanceFactors(
    val amenities: Int,
    val hotelRating: Int,
    val location: Int,
    val price: Int
)