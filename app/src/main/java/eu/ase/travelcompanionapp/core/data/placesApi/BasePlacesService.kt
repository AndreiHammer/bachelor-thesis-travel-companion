package eu.ase.travelcompanionapp.core.data.placesApi

import android.content.Context
import androidx.collection.LruCache
import coil3.Bitmap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

abstract class BasePlacesService(
    context: Context,
    cacheSize: Int = 20
) {
    companion object {
        // TOGGLE THIS FLAG TO ENABLE/DISABLE PLACES API CALLS
        // Set to false to avoid API costs during testing
        private const val ENABLE_PLACES_API = true
    }

    protected val placesClient: PlacesClient = Places.createClient(context)
    private val imageCache = LruCache<String, Bitmap>(maxOf(1, cacheSize))
    private val isCachingEnabled = cacheSize > 0

    protected fun isApiEnabled(): Boolean = ENABLE_PLACES_API

    protected suspend fun searchPlace(
        searchQueries: List<String>,
        placeTypes: List<String>? = null,
        countries: List<String>? = null
    ): String? = suspendCancellableCoroutine { continuation ->
        searchPlaceRecursive(searchQueries, 0, placeTypes, countries) { placeId ->
            continuation.resume(placeId)
        }
    }

    private fun searchPlaceRecursive(
        searchQueries: List<String>,
        index: Int,
        placeTypes: List<String>?,
        countries: List<String>?,
        callback: (String?) -> Unit
    ) {
        if (index >= searchQueries.size) {
            callback(null)
            return
        }

        val requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setQuery(searchQueries[index])

        placeTypes?.let { requestBuilder.setTypesFilter(it) }
        countries?.let { requestBuilder.setCountries(it) }

        val request = requestBuilder.build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val prediction = response.autocompletePredictions.firstOrNull()
                if (prediction != null) {
                    callback(prediction.placeId)
                } else {
                    // Try next search query
                    searchPlaceRecursive(searchQueries, index + 1, placeTypes, countries, callback)
                }
            }
            .addOnFailureListener {
                searchPlaceRecursive(searchQueries, index + 1, placeTypes, countries, callback)
            }
    }

    protected suspend fun fetchPlacePhotos(
        placeId: String,
        maxPhotos: Int = 1,
        maxWidth: Int = 500,
        maxHeight: Int = 300
    ): List<Bitmap> = suspendCancellableCoroutine { continuation ->
        val fields = listOf(Place.Field.PHOTO_METADATAS)
        val fetchRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(fetchRequest)
            .addOnSuccessListener { placeResponse ->
                val photoMetadatas = placeResponse.place.photoMetadatas

                if (photoMetadatas.isNullOrEmpty()) {
                    continuation.resume(emptyList())
                    return@addOnSuccessListener
                }

                val photosToFetch = photoMetadatas.take(maxPhotos)
                val fetchedPhotos = mutableListOf<Bitmap>()
                var completedRequests = 0

                photosToFetch.forEach { metadata ->
                    fetchPhotoForMetadata(metadata, maxWidth, maxHeight) { bitmap ->
                        bitmap?.let { fetchedPhotos.add(it) }
                        completedRequests++

                        if (completedRequests == photosToFetch.size) {
                            continuation.resume(fetchedPhotos)
                        }
                    }
                }
            }
            .addOnFailureListener {
                continuation.resume(emptyList())
            }
    }

    protected fun fetchPhotoForMetadata(
        photoMetadata: PhotoMetadata,
        maxWidth: Int = 500,
        maxHeight: Int = 300,
        callback: (Bitmap?) -> Unit
    ) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(maxWidth)
            .setMaxHeight(maxHeight)
            .build()

        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { response ->
                callback(response.bitmap)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    protected fun getCachedImage(key: String): Bitmap? = 
        if (isCachingEnabled) imageCache[key] else null

    protected fun cacheImage(key: String, bitmap: Bitmap) {
        if (isCachingEnabled) {
            imageCache.put(key, bitmap)
        }
    }
} 