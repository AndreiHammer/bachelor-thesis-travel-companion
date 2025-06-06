package eu.ase.travelcompanionapp.recommendation.data.placesApi

import android.content.Context
import androidx.collection.LruCache
import coil3.Bitmap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes.CITIES
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationThumbnailRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume

@OptIn(DelicateCoroutinesApi::class)
class DestinationThumbnailService(context: Context): DestinationThumbnailRepository {
    
    companion object {
        // TOGGLE THIS FLAG TO ENABLE/DISABLE PLACES API PHOTO FETCHING
        // Set to false to avoid API costs during testing
        private const val ENABLE_PLACES_API = false
    }
    
    private val placesClient: PlacesClient = Places.createClient(context)
    private val imageCache = LruCache<String, Bitmap>(30)

    private val requestCounter = AtomicInteger(0)
    private val maxRequestsPerDay = 150

    init {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.add(Calendar.HOUR_OF_DAY, 0)
        calendar.add(Calendar.MINUTE, 0)
        calendar.add(Calendar.SECOND, 0)

        val delay = calendar.timeInMillis - now

        GlobalScope.launch {
            delay(delay)
            requestCounter.set(0)
        }
    }

    override suspend fun getDestinationThumbnail(destination: Destination): Bitmap? {
        // Return null immediately if Places API is disabled
        if (!ENABLE_PLACES_API) {
            return null
        }
        
        if (requestCounter.get() >= maxRequestsPerDay) {
            return null
        }

        val cacheKey = "${destination.city}_${destination.country}"
        imageCache[cacheKey]?.let { return it }

        requestCounter.incrementAndGet()

        return suspendCancellableCoroutine { continuation ->
            val searchQuery = "${destination.city}, ${destination.country}"
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(searchQuery)
                .setTypesFilter(listOf(CITIES))
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val prediction = response.autocompletePredictions.firstOrNull()
                    if(prediction == null) {
                        searchFallback(destination.city) { bitmap ->
                            bitmap?.let { imageCache.put(cacheKey, it) }
                            continuation.resume(bitmap)
                        }
                        return@addOnSuccessListener
                    }

                    val placeId = prediction.placeId
                    val fields = listOf(Place.Field.PHOTO_METADATAS)
                    val fetchRequest = FetchPlaceRequest.newInstance(placeId, fields)

                    placesClient.fetchPlace(fetchRequest)
                        .addOnSuccessListener { placeResponse ->
                            val photoMetadatas = placeResponse.place.photoMetadatas

                            if(photoMetadatas.isNullOrEmpty()) {
                                continuation.resume(null)
                                return@addOnSuccessListener
                            }

                            val photoMetadata = photoMetadatas[0]
                            fetchPhotoForMetadata(photoMetadata) { bitmap ->
                                bitmap?.let { imageCache.put(cacheKey, it) }
                                continuation.resume(bitmap)
                            }
                        }
                        .addOnFailureListener {
                            continuation.resume(null)
                        }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    private fun searchFallback(cityName: String, callback: (Bitmap?) -> Unit) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(cityName)
            .setTypesFilter(listOf(CITIES))
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val prediction = response.autocompletePredictions.firstOrNull()
                if(prediction == null) {
                    callback(null)
                    return@addOnSuccessListener
                }

                val placeId = prediction.placeId
                val fields = listOf(Place.Field.PHOTO_METADATAS)
                val fetchRequest = FetchPlaceRequest.newInstance(placeId, fields)

                placesClient.fetchPlace(fetchRequest)
                    .addOnSuccessListener { placeResponse ->
                        val photoMetadatas = placeResponse.place.photoMetadatas

                        if(photoMetadatas.isNullOrEmpty()) {
                            callback(null)
                            return@addOnSuccessListener
                        }

                        val photoMetadata = photoMetadatas[0]
                        fetchPhotoForMetadata(photoMetadata, callback)
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    private fun fetchPhotoForMetadata(photoMetadata: PhotoMetadata, callback: (Bitmap?) -> Unit) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(800)
            .setMaxHeight(600)
            .build()

        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { response ->
                callback(response.bitmap)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
} 