package eu.ase.travelcompanionapp.hotel.domain.repository

interface CityToIATACodeRepository {
    fun getIATACode(city: String): String?

    fun getCitySuggestions(): List<String>
}