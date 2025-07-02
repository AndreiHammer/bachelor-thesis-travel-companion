package eu.ase.travelcompanionapp.hotel.data.placesApi

import android.content.Context
import coil3.Bitmap
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import eu.ase.travelcompanionapp.core.data.placesApi.BasePlacesService
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result.Error
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result.Success
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.domain.model.PlaceDetails
import eu.ase.travelcompanionapp.hotel.domain.model.Review
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PlacesApiService(context: Context): BasePlacesService(
    context = context,
    cacheSize = 5
) {

    suspend fun getHotelDetails(
        hotelName: String, 
        country: String, 
        onResult: (Result<PlaceDetails, DataError>) -> Unit
    ) {
        if (!isApiEnabled()) {
            onResult(Error(DataError.Remote.UNKNOWN))
            return
        }

        try {
            val searchQueries = buildHotelSearchQueries(hotelName, country)
            val placeTypes = listOf(
                PlaceTypes.LODGING,
                PlaceTypes.ESTABLISHMENT
            )
            val countries = listOfNotNull(country.takeIf { it.isNotBlank() })

            val placeId = searchPlace(
                searchQueries = searchQueries,
                placeTypes = placeTypes,
                countries = countries
            )

            if (placeId == null) {
                onResult(Error(DataError.Remote.UNKNOWN))
                return
            }

            fetchDetailedPlaceInfo(placeId, hotelName, country, onResult)

        } catch (e: Exception) {
            onResult(Error(DataError.Remote.UNKNOWN))
        }
    }

    private suspend fun fetchDetailedPlaceInfo(
        placeId: String,
        hotelName: String,
        country: String,
        onResult: (Result<PlaceDetails, DataError>) -> Unit
    ) = suspendCancellableCoroutine { continuation ->
        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.LOCATION,
            Place.Field.PHOTO_METADATAS,
            Place.Field.REVIEWS,
            Place.Field.RATING,
            Place.Field.INTERNATIONAL_PHONE_NUMBER
        )
        val fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(fetchPlaceRequest)
            .addOnSuccessListener { response ->
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
                val placeDetails = PlaceDetails(hotel, mutableListOf(), rating, reviews)

                if (photoMetadatas.isNullOrEmpty()) {
                    placeDetails.photos = emptyList()
                    onResult(Success(placeDetails))
                    continuation.resume(Unit)
                } else {
                    val photosToFetch = photoMetadatas.take(10)
                    val photosUris = mutableListOf<Bitmap>()
                    var completedRequests = 0

                    photosToFetch.forEach { metadata ->
                        fetchPhotoForMetadata(metadata, 800, 600) { bitmap ->
                            bitmap?.let { photosUris.add(it) }
                            completedRequests++

                            if (completedRequests == photosToFetch.size) {
                                placeDetails.photos = photosUris
                                onResult(Success(placeDetails))
                                continuation.resume(Unit)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                onResult(Error(DataError.Remote.UNKNOWN))
                continuation.resume(Unit)
            }
    }

    private fun buildHotelSearchQueries(hotelName: String, country: String): List<String> {
        val queries = mutableListOf<String>()

        queries.add("$hotelName, $country")

        queries.add(hotelName)

        queries.add("$hotelName hotel")

        if (country.isNotBlank()) {
            queries.add("$hotelName $country")
        }

        val simplifiedName = hotelName
            .replace(Regex("\\b(hotel|inn|resort|lodge|suites?)\\b", RegexOption.IGNORE_CASE), "")
            .trim()
        if (simplifiedName.isNotBlank() && simplifiedName != hotelName) {
            queries.add(simplifiedName)
            if (country.isNotBlank()) {
                queries.add("$simplifiedName, $country")
            }
        }
        
        return queries.distinct()
    }
}