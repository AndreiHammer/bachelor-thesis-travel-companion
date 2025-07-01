package eu.ase.travelcompanionapp.recommendation.data.travelpreferences

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
        private const val PREFERRED_ACTIVITIES_KEY = "preferred_activities"
        private const val CLIMATE_PREFERENCE_KEY = "climate_preference"
        private const val TRAVEL_STYLE_KEY = "travel_style"
        private const val TRIP_DURATION_KEY = "trip_duration"
        private const val COMPANIONS_KEY = "companions"
        private const val CULTURAL_OPENNESS_KEY = "cultural_openness"
        private const val PREFERRED_COUNTRY_KEY = "preferred_country"
        private const val BUCKET_LIST_THEMES_KEY = "bucket_list_themes"
        private const val BUDGET_RANGE_KEY = "budget_range"
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
    
    private fun getPreferredActivitiesKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$PREFERRED_ACTIVITIES_KEY"
        } else {
            "${userId}_$PREFERRED_ACTIVITIES_KEY"
        }
    }
    
    private fun getClimatePreferenceKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$CLIMATE_PREFERENCE_KEY"
        } else {
            "${userId}_$CLIMATE_PREFERENCE_KEY"
        }
    }
    
    private fun getTravelStyleKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$TRAVEL_STYLE_KEY"
        } else {
            "${userId}_$TRAVEL_STYLE_KEY"
        }
    }
    
    private fun getTripDurationKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$TRIP_DURATION_KEY"
        } else {
            "${userId}_$TRIP_DURATION_KEY"
        }
    }
    
    private fun getCompanionsKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$COMPANIONS_KEY"
        } else {
            "${userId}_$COMPANIONS_KEY"
        }
    }
    
    private fun getCulturalOpennessKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$CULTURAL_OPENNESS_KEY"
        } else {
            "${userId}_$CULTURAL_OPENNESS_KEY"
        }
    }
    
    private fun getPreferredCountryKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$PREFERRED_COUNTRY_KEY"
        } else {
            "${userId}_$PREFERRED_COUNTRY_KEY"
        }
    }
    
    private fun getBucketListThemesKey(): String {
        val userId = authManager.currentUserId
        return if (userId.isEmpty()) {
            "guest_$BUCKET_LIST_THEMES_KEY"
        } else {
            "${userId}_$BUCKET_LIST_THEMES_KEY"
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
            val hasCompleted = preferences[booleanPreferencesKey(getQuestionnaireCompletedKey())] ?: false
            if (!hasCompleted) return@map null
            
            val activitiesJson = preferences[stringPreferencesKey(getPreferredActivitiesKey())] ?: "[]"
            val climatePreference = preferences[stringPreferencesKey(getClimatePreferenceKey())] ?: ""
            val travelStyle = preferences[stringPreferencesKey(getTravelStyleKey())] ?: ""
            val tripDuration = preferences[stringPreferencesKey(getTripDurationKey())] ?: ""
            val companions = preferences[stringPreferencesKey(getCompanionsKey())] ?: ""
            val culturalOpenness = preferences[intPreferencesKey(getCulturalOpennessKey())] ?: 5
            val preferredCountry = preferences[stringPreferencesKey(getPreferredCountryKey())] ?: ""
            val bucketListThemesJson = preferences[stringPreferencesKey(getBucketListThemesKey())] ?: "[]"
            val budgetRange = preferences[stringPreferencesKey(getBudgetRangeKey())] ?: ""
            val continentsJson = preferences[stringPreferencesKey(getPreferredContinentsKey())] ?: "[]"
            
            val listType = object : TypeToken<ArrayList<String>>() {}.type
            
            val activities: ArrayList<String> = gson.fromJson(activitiesJson, listType) ?: arrayListOf()
            val bucketListThemes: ArrayList<String> = gson.fromJson(bucketListThemesJson, listType) ?: arrayListOf()
            val continents: ArrayList<String> = gson.fromJson(continentsJson, listType) ?: arrayListOf()
            
            QuestionnaireResponse(
                preferredActivities = activities,
                climatePreference = climatePreference,
                travelStyle = travelStyle,
                tripDuration = tripDuration,
                companions = companions,
                culturalOpenness = culturalOpenness,
                preferredCountry = preferredCountry,
                bucketListThemes = bucketListThemes,
                budgetRange = budgetRange,
                preferredContinents = continents
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveQuestionnaireResponse(response: QuestionnaireResponse) {
        context.recommendationPreferencesDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(getQuestionnaireCompletedKey())] = true
            preferences[stringPreferencesKey(getPreferredActivitiesKey())] = gson.toJson(response.preferredActivities)
            preferences[stringPreferencesKey(getClimatePreferenceKey())] = response.climatePreference.takeIf { it.isNotBlank() } ?: ""
            preferences[stringPreferencesKey(getTravelStyleKey())] = response.travelStyle.takeIf { it.isNotBlank() } ?: ""
            preferences[stringPreferencesKey(getTripDurationKey())] = response.tripDuration.takeIf { it.isNotBlank() } ?: ""
            preferences[stringPreferencesKey(getCompanionsKey())] = response.companions.takeIf { it.isNotBlank() } ?: ""
            preferences[intPreferencesKey(getCulturalOpennessKey())] = response.culturalOpenness
            preferences[stringPreferencesKey(getPreferredCountryKey())] = response.preferredCountry
            preferences[stringPreferencesKey(getBucketListThemesKey())] = gson.toJson(response.bucketListThemes)
            preferences[stringPreferencesKey(getBudgetRangeKey())] = response.budgetRange.takeIf { it.isNotBlank() } ?: ""
            preferences[stringPreferencesKey(getPreferredContinentsKey())] = gson.toJson(response.preferredContinents)
        }
    }
    
    override suspend fun clearQuestionnaireResponse() {
        context.recommendationPreferencesDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(getQuestionnaireCompletedKey())] = false
            preferences.remove(stringPreferencesKey(getPreferredActivitiesKey()))
            preferences.remove(stringPreferencesKey(getClimatePreferenceKey()))
            preferences.remove(stringPreferencesKey(getTravelStyleKey()))
            preferences.remove(stringPreferencesKey(getTripDurationKey()))
            preferences.remove(stringPreferencesKey(getCompanionsKey()))
            preferences.remove(intPreferencesKey(getCulturalOpennessKey()))
            preferences.remove(stringPreferencesKey(getPreferredCountryKey()))
            preferences.remove(stringPreferencesKey(getBucketListThemesKey()))
            preferences.remove(stringPreferencesKey(getBudgetRangeKey()))
            preferences.remove(stringPreferencesKey(getPreferredContinentsKey()))
        }
    }
    
    override suspend fun hasQuestionnaire(): Boolean {
        return hasCompletedQuestionnaire.first()
    }
} 