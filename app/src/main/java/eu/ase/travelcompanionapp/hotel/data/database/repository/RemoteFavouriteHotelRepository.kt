package eu.ase.travelcompanionapp.hotel.data.database.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import kotlinx.coroutines.tasks.await

class RemoteFavouriteHotelRepository {
    private val firestore = Firebase.firestore

    private fun getUserHotelsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("hotels")

    private fun Map<String, Any?>.getStringList(key: String): List<String> {
        return when (val value = this[key]) {
            is List<*> -> value.mapNotNull { it as? String }
            is Array<*> -> value.mapNotNull { it as? String }
            is String -> listOf(value)
            null -> emptyList()
            else -> emptyList()
        }
    }

    suspend fun getFavouriteHotels(userId: String): List<Hotel> {
        return try{
            val snapshot = getUserHotelsCollection(userId).get().await()

            snapshot.documents.mapNotNull { document ->
                val hotelData = document.data?: return@mapNotNull null
                val amenities = hotelData.getStringList("amenities")

                Hotel(
                    hotelId = hotelData["hotelId"] as String,
                    chainCode = hotelData["chainCode"] as? String,
                    iataCode = hotelData["iataCode"] as String,
                    dupeId = (hotelData["dupeId"] as? Number)?.toInt(),
                    name = hotelData["name"] as String,
                    latitude = (hotelData["latitude"] as Number).toDouble(),
                    longitude = (hotelData["longitude"] as Number).toDouble(),
                    countryCode = hotelData["countryCode"] as String,
                    amenities = ArrayList(amenities),
                    rating = (hotelData["rating"] as? Number)?.toInt(),
                    giataId = (hotelData["giataId"] as? Number)?.toInt(),
                    phone = hotelData["phone"] as? String
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addFavourite(userId: String, hotel: Hotel, checkInDate: String? = null,
                             checkOutDate: String? = null, adults: Int? = null){
        val hotelData = hashMapOf(
            "userId" to userId,
            "hotelId" to hotel.hotelId,
            "chainCode" to hotel.chainCode,
            "iataCode" to hotel.iataCode,
            "dupeId" to hotel.dupeId,
            "name" to hotel.name,
            "latitude" to hotel.latitude,
            "longitude" to hotel.longitude,
            "countryCode" to hotel.countryCode,
            "amenities" to hotel.amenities,
            "rating" to hotel.rating,
            "giataId" to hotel.giataId,
            "phone" to hotel.phone,
            "checkInDate" to checkInDate,
            "checkOutDate" to checkOutDate,
            "adults" to adults
        )
        getUserHotelsCollection(userId).document(hotel.hotelId).set(hotelData).await()
    }

    suspend fun removeFavourite(userId: String, hotelId: String) {
        getUserHotelsCollection(userId).document(hotelId).delete().await()
    }

    suspend fun isFavourite(userId: String, hotelId: String): Boolean {
        val documentSnapshot = getUserHotelsCollection(userId).document(hotelId).get().await()
        return documentSnapshot.exists()
    }

    suspend fun getHotelDocument(userId: String, hotelId: String): DocumentSnapshot? {
        return try {
            getUserHotelsCollection(userId).document(hotelId).get().await()
        } catch (e: Exception) {
            null
        }
    }
}