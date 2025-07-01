package eu.ase.travelcompanionapp.recommendation.domain.model

data class QuestionnaireResponse(
    val preferredActivities: ArrayList<String> = arrayListOf(),
    val climatePreference: String = "",
    val travelStyle: String = "",
    val tripDuration: String = "",
    val companions: String = "",
    val culturalOpenness: Int = 5,
    val preferredCountry: String = "",
    val bucketListThemes: ArrayList<String> = arrayListOf(),
    val budgetRange: String = "",
    val preferredContinents: ArrayList<String> = arrayListOf()
)