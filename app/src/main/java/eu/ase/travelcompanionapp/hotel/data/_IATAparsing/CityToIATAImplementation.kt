package eu.ase.travelcompanionapp.hotel.data._IATAparsing

import android.content.Context
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository

class CityToIATACodeRepositoryImpl(
    context: Context
) : CityToIATACodeRepository {

    init {
        CityToIATAMapper.loadCityToIATAMap(context)
    }

    override fun getIATACode(city: String): String? {
        return CityToIATAMapper.getIATACode(city)
    }

    override fun getCitySuggestions(): List<String> {
        return CityToIATAMapper.getCityNames()
    }
}