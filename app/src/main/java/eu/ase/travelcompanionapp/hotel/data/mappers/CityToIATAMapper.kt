package eu.ase.travelcompanionapp.hotel.data.mappers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

object CityToIATAMapper {
    private var cityToIATAMap: Map<String, String>? = null

    fun loadCityToIATAMap(context: Context) {
        if (cityToIATAMap == null) {
            val jsonString = context.assets.open("city_iata_map.json").bufferedReader().use(
                BufferedReader::readText)
            val type = object : TypeToken<Map<String, String>>() {}.type
            cityToIATAMap = Gson().fromJson(jsonString, type)
        }
    }

    fun getIATACode(city: String): String? {
        return cityToIATAMap?.get(city)
    }
}