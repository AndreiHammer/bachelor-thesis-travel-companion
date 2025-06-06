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
import eu.ase.travelcompanionapp.hotel.domain.model.PlaceDetails
import eu.ase.travelcompanionapp.hotel.domain.model.Review


class PlacesApiService(context: Context) {
    companion object {
        // TOGGLE THIS FLAG TO ENABLE/DISABLE PLACES API CALLS
        // Set to false to avoid API costs during testing
        private const val ENABLE_PLACES_API = false
    }

    private val placesClient: PlacesClient = Places.createClient(context)

    fun getHotelDetails(hotelName: String, country: String, onResult: (Result<PlaceDetails, DataError>) -> Unit) {
        if (!ENABLE_PLACES_API) {
            onResult(Error(DataError.Remote.UNKNOWN))
            return
        }

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

    private fun fetchHotelDetails(placeId: String, hotelName: String, country: String,
                                  onResult: (Result<PlaceDetails, DataError>) -> Unit) {
        val fields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION,
            Place.Field.PHOTO_METADATAS, Place.Field.REVIEWS
            ,Place.Field.RATING, Place.Field.INTERNATIONAL_PHONE_NUMBER)
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
            val rating = place.rating ?: 0.0
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
            val placeDetails = PlaceDetails(hotel, photosUris, rating, reviews)
            if (photoMetadatas.isNullOrEmpty()) {
                placeDetails.photos = emptyList()
                onResult(Success(placeDetails))
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
                            onResult(Success(placeDetails))
                        }
                    }.addOnFailureListener {
                        if (photosUris.isNotEmpty()) {
                            onResult(Success(placeDetails))
                        } else {
                            onResult(Success(placeDetails))
                        }
                    }
                }
            }
        }.addOnFailureListener {
            onResult(Error(DataError.Remote.UNKNOWN))
        }
    }
}