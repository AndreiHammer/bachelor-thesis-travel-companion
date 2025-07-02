package eu.ase.travelcompanionapp.hotel.data.placesApi

import android.content.Context
import coil3.Bitmap
import com.google.android.libraries.places.api.model.PlaceTypes
import eu.ase.travelcompanionapp.core.data.placesApi.BasePlacesService
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository

class HotelThumbnailService(context: Context): BasePlacesService(
    context = context,
    cacheSize = 30
), HotelThumbnailRepository {

    override suspend fun getHotelThumbnail(hotel: Hotel): Bitmap? {
        if (!isApiEnabled()) {
            return null
        }

        getCachedImage(hotel.hotelId)?.let { return it }

        val searchQueries = buildSearchQueries(hotel)
        val placeTypes = listOf(
            PlaceTypes.LODGING,
            PlaceTypes.ESTABLISHMENT
        )
        val countries = listOfNotNull(hotel.countryCode.takeIf { it.isNotBlank() })

        val placeId = searchPlace(
            searchQueries = searchQueries,
            placeTypes = placeTypes,
            countries = countries
        ) ?: return null

        val photos = fetchPlacePhotos(
            placeId = placeId,
            maxPhotos = 1,
            maxWidth = 600,
            maxHeight = 400
        )

        return photos.firstOrNull()?.also { bitmap ->
            cacheImage(hotel.hotelId, bitmap)
        }
    }

    private fun buildSearchQueries(hotel: Hotel): List<String> {
        val queries = mutableListOf<String>()

        queries.add(hotel.name)

        if (hotel.countryCode.isNotBlank()) {
            queries.add("${hotel.name}, ${hotel.countryCode}")
        }

        queries.add("${hotel.name} hotel")

        hotel.chainCode?.let { chainCode ->
            queries.add("$chainCode ${hotel.name}")
        }

        val simplifiedName = hotel.name
            .replace(Regex("\\b(hotel|inn|resort|lodge|suites?)\\b", RegexOption.IGNORE_CASE), "")
            .trim()
        if (simplifiedName.isNotBlank() && simplifiedName != hotel.name) {
            queries.add(simplifiedName)
        }
        
        return queries.distinct()
    }
}