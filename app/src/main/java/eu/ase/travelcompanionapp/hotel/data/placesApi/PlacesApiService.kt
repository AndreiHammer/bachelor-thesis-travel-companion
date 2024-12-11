package eu.ase.travelcompanionapp.hotel.data.placesApi

import android.content.Context
import coil3.Bitmap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import eu.ase.travelcompanionapp.core.domain.DataError
import eu.ase.travelcompanionapp.core.domain.Result
import eu.ase.travelcompanionapp.core.domain.Result.Error
import eu.ase.travelcompanionapp.core.domain.Result.Success
import eu.ase.travelcompanionapp.hotel.domain.Hotel


class PlacesApiService(context: Context) {
    private val placesClient: PlacesClient = Places.createClient(context)

    fun getHotelDetails(hotelName: String, country: String, onResult: (Result<Pair<Hotel, List<Bitmap>>, DataError>) -> Unit) {
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
            .addOnFailureListener { exception ->
                onResult(Error(DataError.Remote.UNKNOWN))
            }
    }

    private fun fetchHotelDetails(placeId: String, hotelName: String, country: String, onResult: (Result<Pair<Hotel, List<Bitmap>>, DataError>) -> Unit) {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS)
        val fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { response ->
            val place = response.place
            val hotel = Hotel(
                hotelId = place.id ?: "",
                chainCode = null,
                iataCode = "",
                dupeId = null,
                name = place.name ?: hotelName,
                latitude = place.latLng?.latitude ?: 0.0,
                longitude = place.latLng?.longitude ?: 0.0,
                countryCode = country,
                amenities = arrayListOf(),
                rating = place.rating?.toInt(),
                giataId = null,
                phone = place.phoneNumber
            )

            val photoMetadatas = place.photoMetadatas
            val photosUris = mutableListOf<Bitmap>()

            if (photoMetadatas.isNullOrEmpty()) {
                onResult(Error(DataError.Remote.UNKNOWN))
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
                            onResult(Success(Pair(hotel, photosUris)))
                        }
                    }.addOnFailureListener { exception ->
                        onResult(Error(DataError.Remote.UNKNOWN))
                    }
                }
            }
        }.addOnFailureListener { exception ->
            onResult(Error(DataError.Remote.UNKNOWN))
        }
    }
}