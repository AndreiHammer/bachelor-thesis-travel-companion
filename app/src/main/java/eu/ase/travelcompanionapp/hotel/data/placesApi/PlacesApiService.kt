package eu.ase.travelcompanionapp.hotel.data.placesApi

import android.content.Context
import coil3.Bitmap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result.Error
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result.Success
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.model.Review


class PlacesApiService(context: Context) {
    private val placesClient: PlacesClient = Places.createClient(context)

    fun getHotelDetails(hotelName: String, country: String, onResult: (Result<Triple<Hotel, List<Bitmap>, List<Review>>, DataError>) -> Unit) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(hotelName)
            .setCountries(country)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val prediction = response.autocompletePredictions.firstOrNull()
                prediction?.let {
                    val placeId = it.placeId
                    fetchHotelDetails(placeId, hotelName, country, onResult)
                } ?: onResult(Error(DataError.Remote.UNKNOWN))
            }
            .addOnFailureListener {
                onResult(Error(DataError.Remote.UNKNOWN))
            }
    }

    private fun fetchHotelDetails(placeId: String, hotelName: String, country: String, onResult: (Result<Triple<Hotel, List<Bitmap>, List<Review>>, DataError>) -> Unit) {
        val fields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION, Place.Field.PHOTO_METADATAS, Place.Field.REVIEWS)
        val fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { response ->
            val place = response.place

            val latitude = place.location?.latitude ?: 0.0
            val longitude = place.location?.longitude ?: 0.0
            val hotel = Hotel(
                hotelId = place.id ?: "",
                chainCode = null,
                iataCode = "",
                dupeId = null,
                name = place.displayName ?: hotelName,
                latitude = latitude,
                longitude = longitude,
                countryCode = country,
                amenities = arrayListOf(),
                rating = place.rating?.toInt(),
                giataId = null,
                phone = place.internationalPhoneNumber
            )

            val reviews = place.reviews?.map { review ->
                Review(
                    authorName = review.authorAttribution.name,
                    photoUri = review.authorAttribution.photoUri,
                    rating = review.rating,
                    text = review.text ?: "",
                    time = review.publishTime!!,
                    relativeTime = review.relativePublishTimeDescription!!
                )
            } ?: emptyList()

            val photoMetadatas = place.photoMetadatas
            val photosUris = mutableListOf<Bitmap>()

            if (photoMetadatas.isNullOrEmpty()) {
                onResult(Success(Triple(hotel, emptyList(), reviews)))
            } else {
                val photosToFetch = photoMetadatas.take(10)

                photoMetadatas.forEach { metadata ->
                    val photoRequest = FetchPhotoRequest.builder(metadata)
                        .setMaxWidth(800)
                        .setMaxHeight(600)
                        .build()

                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener { photoResponse ->
                        photosUris.add(photoResponse.bitmap)

                        if (photosUris.size == photosToFetch.size) {
                            onResult(Success(Triple(hotel, photosUris, reviews)))
                        }
                    }.addOnFailureListener {
                        if (photosUris.isNotEmpty()) {
                            onResult(Success(Triple(hotel, photosUris, reviews)))
                        } else {
                            onResult(Success(Triple(hotel, emptyList(), reviews)))
                        }
                    }
                }
            }
        }.addOnFailureListener {
            onResult(Error(DataError.Remote.UNKNOWN))
        }
    }
}