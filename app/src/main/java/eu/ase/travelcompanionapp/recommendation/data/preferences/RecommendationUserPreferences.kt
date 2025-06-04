package eu.ase.travelcompanionapp.recommendation.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.ase.travelcompanionapp.auth.data.auth.AuthManager
import eu.ase.travelcompanionapp.recommendation.domain.model.ImportanceFactors
import eu.ase.travelcompanionapp.recommendation.domain.model.QuestionnaireResponse
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.recommendationPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "recommendation_preferences"
)

class RecommendationUserPreferences(
    private val context: Context,
): UserPreferencesRepository {
    private val authManager = AuthManager()
    private val gson = Gson()

    companion object {
        private const val QUESTIONNAIRE_COMPLETED_KEY = "questionnaire_completed"
        private const val BUDGET_RANGE_KEY = "budget_range"
        private const val TRAVEL_PURPOSE_KEY = "travel_purpose"
        private const val GROUP_SIZE_KEY = "group_size"
        private const val ACCOMMODATION_TYPE_KEY = "accommodation_type"
        private const val LOCATION_PREFERENCE_KEY = "location_preference"
        private const val IMPORTANCE_AMENITIES_KEY = "importance_amenities"
        private const val IMPORTANCE_HOTEL_RATING_KEY = "importance_hotel_rating"
        private const val IMPORTANCE_LOCATION_KEY = "importance_location"
        private const val IMPORTANCE_PRICE_KEY = "importance_price"
        private const val IMPORTANT_AMENITIES_KEY = "important_amenities"
        private const val PREFERRED_CONTINENTS_KEY = "preferred_continents"
    }

    private fun getQuestionnaireCompletedKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$QUESTIONNAIRE_COMPLETED_KEY"
        } else {
            "${userId}_$QUESTIONNAIRE_COMPLETED_KEY"
        }
    }
    
    private fun getBudgetRangeKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$BUDGET_RANGE_KEY"
        } else {
            "${userId}_$BUDGET_RANGE_KEY"
        }
    }
    
    private fun getTravelPurposeKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$TRAVEL_PURPOSE_KEY"
        } else {
            "${userId}_$TRAVEL_PURPOSE_KEY"
        }
    }
    
    private fun getGroupSizeKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$GROUP_SIZE_KEY"
        } else {
            "${userId}_$GROUP_SIZE_KEY"
        }
    }
    
    private fun getAccommodationTypeKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$ACCOMMODATION_TYPE_KEY"
        } else {
            "${userId}_$ACCOMMODATION_TYPE_KEY"
        }
    }
    
    private fun getLocationPreferenceKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$LOCATION_PREFERENCE_KEY"
        } else {
            "${userId}_$LOCATION_PREFERENCE_KEY"
        }
    }
    
    private fun getImportanceAmenitiesKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$IMPORTANCE_AMENITIES_KEY"
        } else {
            "${userId}_$IMPORTANCE_AMENITIES_KEY"
        }
    }
    
    private fun getImportanceHotelRatingKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$IMPORTANCE_HOTEL_RATING_KEY"
        } else {
            "${userId}_$IMPORTANCE_HOTEL_RATING_KEY"
        }
    }
    
    private fun getImportanceLocationKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$IMPORTANCE_LOCATION_KEY"
        } else {
            "${userId}_$IMPORTANCE_LOCATION_KEY"
        }
    }
    
    private fun getImportancePriceKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$IMPORTANCE_PRICE_KEY"
        } else {
            "${userId}_$IMPORTANCE_PRICE_KEY"
        }
    }
    
    private fun getImportantAmenitiesKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$IMPORTANT_AMENITIES_KEY"
        } else {
            "${userId}_$IMPORTANT_AMENITIES_KEY"
        }
    }
    
    private fun getPreferredContinentsKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$PREFERRED_CONTINENTS_KEY"
        } else {
            "${userId}_$PREFERRED_CONTINENTS_KEY"
        }
    }
    
    override val hasCompletedQuestionnaire: Flow<Boolean> = context.recommendationPreferencesDataStore.data.map { preferences ->
        val key = booleanPreferencesKey(getQuestionnaireCompletedKey())
        preferences[key] ?: false
    }
    
    override val questionnaireResponse: Flow<QuestionnaireResponse?> = context.recommendationPreferencesDataStore.data.map { preferences ->
        try {
            val budgetRange = preferences[stringPreferencesKey(getBudgetRangeKey())] ?: return@map null
            val travelPurpose = preferences[stringPreferencesKey(getTravelPurposeKey())] ?: return@map null
            val groupSize = preferences[stringPreferencesKey(getGroupSizeKey())] ?: return@map null
            val accommodationType = preferences[stringPreferencesKey(getAccommodationTypeKey())] ?: return@map null
            val locationPreference = preferences[stringPreferencesKey(getLocationPreferenceKey())] ?: return@map null
            
            val amenitiesImportance = preferences[intPreferencesKey(getImportanceAmenitiesKey())] ?: 5
            val hotelRatingImportance = preferences[intPreferencesKey(getImportanceHotelRatingKey())] ?: 5
            val locationImportance = preferences[intPreferencesKey(getImportanceLocationKey())] ?: 5
            val priceImportance = preferences[intPreferencesKey(getImportancePriceKey())] ?: 5
            
            val amenitiesJson = preferences[stringPreferencesKey(getImportantAmenitiesKey())] ?: "[]"
            val continentsJson = preferences[stringPreferencesKey(getPreferredContinentsKey())] ?: "[]"
            
            val amenitiesType = object : TypeToken<ArrayList<String>>() {}.type
            val continentsType = object : TypeToken<ArrayList<String>>() {}.type
            
            val amenities: ArrayList<String> = gson.fromJson(amenitiesJson, amenitiesType) ?: arrayListOf()
            val continents: ArrayList<String> = gson.fromJson(continentsJson, continentsType) ?: arrayListOf()
            
            QuestionnaireResponse(
                budgetRange = budgetRange,
                travelPurpose = travelPurpose,
                groupSize = groupSize,
                accommodationType = accommodationType,
                locationPreference = locationPreference,
                importanceFactors = ImportanceFactors(
                    amenities = amenitiesImportance,
                    hotelRating = hotelRatingImportance,
                    location = locationImportance,
                    price = priceImportance
                ),
                importantAmenities = amenities,
                preferredContinents = continents
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveQuestionnaireResponse(response: QuestionnaireResponse) {
        context.recommendationPreferencesDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(getQuestionnaireCompletedKey())] = true
            preferences[stringPreferencesKey(getBudgetRangeKey())] = response.budgetRange
            preferences[stringPreferencesKey(getTravelPurposeKey())] = response.travelPurpose
            preferences[stringPreferencesKey(getGroupSizeKey())] = response.groupSize
            preferences[stringPreferencesKey(getAccommodationTypeKey())] = response.accommodationType
            preferences[stringPreferencesKey(getLocationPreferenceKey())] = response.locationPreference
            preferences[intPreferencesKey(getImportanceAmenitiesKey())] = response.importanceFactors.amenities
            preferences[intPreferencesKey(getImportanceHotelRatingKey())] = response.importanceFactors.hotelRating
            preferences[intPreferencesKey(getImportanceLocationKey())] = response.importanceFactors.location
            preferences[intPreferencesKey(getImportancePriceKey())] = response.importanceFactors.price
            preferences[stringPreferencesKey(getImportantAmenitiesKey())] = gson.toJson(response.importantAmenities)
            preferences[stringPreferencesKey(getPreferredContinentsKey())] = gson.toJson(response.preferredContinents)
        }
    }
    
    override suspend fun clearQuestionnaireResponse() {
        context.recommendationPreferencesDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(getQuestionnaireCompletedKey())] = false
            preferences.remove(stringPreferencesKey(getBudgetRangeKey()))
            preferences.remove(stringPreferencesKey(getTravelPurposeKey()))
            preferences.remove(stringPreferencesKey(getGroupSizeKey()))
            preferences.remove(stringPreferencesKey(getAccommodationTypeKey()))
            preferences.remove(stringPreferencesKey(getLocationPreferenceKey()))
            preferences.remove(intPreferencesKey(getImportanceAmenitiesKey()))
            preferences.remove(intPreferencesKey(getImportanceHotelRatingKey()))
            preferences.remove(intPreferencesKey(getImportanceLocationKey()))
            preferences.remove(intPreferencesKey(getImportancePriceKey()))
            preferences.remove(stringPreferencesKey(getImportantAmenitiesKey()))
            preferences.remove(stringPreferencesKey(getPreferredContinentsKey()))
        }
    }
    
    override suspend fun hasQuestionnaire(): Boolean {
        return hasCompletedQuestionnaire.first()
    }
} 