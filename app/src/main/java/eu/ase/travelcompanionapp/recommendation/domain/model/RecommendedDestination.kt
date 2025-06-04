package eu.ase.travelcompanionapp.recommendation.domain.model

data class RecommendedDestination(
    val userId: String,
    val destinations: ArrayList<Destination> = arrayListOf(),
    val generatedAt: String,
    val reasoning: String
)
