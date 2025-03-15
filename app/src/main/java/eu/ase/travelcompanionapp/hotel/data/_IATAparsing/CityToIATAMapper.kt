package eu.ase.travelcompanionapp.hotel.data._IATAparsing

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

data class CityInfo(
    val code: String,
    val country: String
)


object CityToIATAMapper {
    private var cityToIATAMap: Map<String, CityInfo>? = null

    fun loadCityToIATAMap(context: Context) {
        if (cityToIATAMap == null) {
            val jsonString = context.assets.open("city_iata_map.json").bufferedReader().use(
                BufferedReader::readText)
            val type = object : TypeToken<Map<String, CityInfo>>() {}.type
            cityToIATAMap = Gson().fromJson(jsonString, type)
        }
    }

    fun getIATACode(city: String): String? {
        return cityToIATAMap?.get(city)?.code
    }

    fun getCityWithCountryList(): List<Pair<String, String>> {
        return cityToIATAMap?.map { (city, info) ->
            Pair(city, info.country)
        } ?: emptyList()
    }
}