package eu.ase.travelcompanionapp.recommendation.data.placesApi

import android.content.Context
import coil3.Bitmap
import com.google.android.libraries.places.api.model.PlaceTypes.CITIES
import eu.ase.travelcompanionapp.core.data.placesApi.BasePlacesService
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationThumbnailRepository

class DestinationThumbnailService(context: Context): BasePlacesService(
    context = context,
    cacheSize = 40
), DestinationThumbnailRepository {

    override suspend fun getDestinationThumbnail(destination: Destination): Bitmap? {
        if (!isApiEnabled()) {
            return null
        }
        
        val cacheKey = "${destination.city}_${destination.country}"
        getCachedImage(cacheKey)?.let { return it }

        val searchQueries = buildDestinationSearchQueries(destination)
        val placeTypes = listOf(CITIES)

        val placeId = searchPlace(
            searchQueries = searchQueries,
            placeTypes = placeTypes,
            countries = null
        ) ?: return null

        val photos = fetchPlacePhotos(
            placeId = placeId,
            maxPhotos = 1,
            maxWidth = 800,
            maxHeight = 600
        )

        return photos.firstOrNull()?.also { bitmap ->
            cacheImage(cacheKey, bitmap)
        }
    }

    private fun buildDestinationSearchQueries(destination: Destination): List<String> {
        val queries = mutableListOf<String>()

        queries.add("${destination.city}, ${destination.country}")

        queries.add(destination.city)

        if (destination.country.length == 2) {
            queries.add("${destination.city} city")
        } else {
            queries.add("${destination.city} tourism")
        }

        val cityVariations = listOf(
            "${destination.city} city center",
            "${destination.city} downtown",
            "${destination.city} historic center"
        )
        queries.addAll(cityVariations)
        
        return queries.distinct()
    }
} 