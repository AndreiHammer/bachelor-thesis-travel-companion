package eu.ase.travelcompanionapp.hotel.data.placesApi

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
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume

@OptIn(DelicateCoroutinesApi::class)
class HotelThumbnailService(context: Context): HotelThumbnailRepository {
    private val placesClient: PlacesClient = Places.createClient(context)
    private val imageCache = LruCache<String, Bitmap>(20)

    private val requestCounter = AtomicInteger(0)
    private val maxRequestsPerDay = 100

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

    override suspend fun getHotelThumbnail(hotel: Hotel): Bitmap? {
        if (requestCounter.get() >= maxRequestsPerDay) {
            return null
        }

        imageCache[hotel.hotelId]?.let { return it }

        requestCounter.incrementAndGet()

        return suspendCancellableCoroutine { continuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(hotel.name)
                .setCountries(hotel.countryCode)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val prediction = response.autocompletePredictions.firstOrNull()
                    if(prediction == null) {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }

                    val placeId = prediction.placeId
                    val fields = listOf(Place.Field.PHOTO_METADATAS)
                    val fetchRequest = FetchPlaceRequest.newInstance(placeId, fields)

                    placesClient.fetchPlace(fetchRequest)
                        .addOnSuccessListener { placeResponse ->
                            val photoMedatas = placeResponse.place.photoMetadatas

                            if(photoMedatas.isNullOrEmpty()) {
                                continuation.resume(null)
                                return@addOnSuccessListener
                            }

                            val photoMetadata = photoMedatas[0]
                            fetchPhotoForMetadata(photoMetadata) { bitmap ->
                                bitmap?.let { imageCache.put(hotel.hotelId, it) }
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

    private fun fetchPhotoForMetadata(photoMetadata: PhotoMetadata, callback: (Bitmap?) -> Unit) {
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(500)
            .setMaxHeight(300)
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