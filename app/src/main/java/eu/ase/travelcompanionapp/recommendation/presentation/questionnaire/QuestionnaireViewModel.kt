package eu.ase.travelcompanionapp.recommendation.presentation.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.recommendation.domain.model.ImportanceFactors
import eu.ase.travelcompanionapp.recommendation.domain.model.QuestionnaireResponse
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class QuestionnaireViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionnaireState())
    val state: StateFlow<QuestionnaireState> = _state.asStateFlow()

    fun initializeForMode(isEditMode: Boolean) {
        if (isEditMode) {
            loadExistingPreferences()
        }
        _state.value = _state.value.copy(isEditMode = isEditMode)
    }

    private fun loadExistingPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.questionnaireResponse.collectLatest { existingResponse ->
                existingResponse?.let { response ->
                    _state.value = _state.value.copy(
                        preferences = response,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateBudgetRange(budgetRange: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(budgetRange = budgetRange)
        )
    }

    fun updateTravelPurpose(travelPurpose: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(travelPurpose = travelPurpose)
        )
    }

    fun updateGroupSize(groupSize: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(groupSize = groupSize)
        )
    }

    fun updateAccommodationType(accommodationType: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(accommodationType = accommodationType)
        )
    }

    fun updateLocationPreference(locationPreference: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(locationPreference = locationPreference)
        )
    }

    fun updatePriceImportance(importance: Int) {
        val currentFactors = _state.value.preferences.importanceFactors
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(
                importanceFactors = currentFactors.copy(price = importance)
            )
        )
    }

    fun updateRatingImportance(importance: Int) {
        val currentFactors = _state.value.preferences.importanceFactors
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(
                importanceFactors = currentFactors.copy(hotelRating = importance)
            )
        )
    }

    fun updateAmenitiesImportance(importance: Int) {
        val currentFactors = _state.value.preferences.importanceFactors
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(
                importanceFactors = currentFactors.copy(amenities = importance)
            )
        )
    }

    fun updateLocationImportance(importance: Int) {
        val currentFactors = _state.value.preferences.importanceFactors
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(
                importanceFactors = currentFactors.copy(location = importance)
            )
        )
    }

    fun updatePreferredAmenities(amenities: List<String>) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(
                importantAmenities = ArrayList(amenities)
            )
        )
    }

    fun updatePreferredContinents(continents: List<String>) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(
                preferredContinents = ArrayList(continents)
            )
        )
    }

    fun savePreferences() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                
                userPreferencesRepository.saveQuestionnaireResponse(_state.value.preferences)
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    isCompleted = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save preferences: ${e.message}"
                )
            }
        }
    }
}

data class QuestionnaireState(
    val preferences: QuestionnaireResponse = QuestionnaireResponse(
        budgetRange = "",
        travelPurpose = "",
        groupSize = "",
        accommodationType = "",
        locationPreference = "",
        importanceFactors = ImportanceFactors(
            amenities = 5,
            hotelRating = 5,
            location = 5,
            price = 5
        ),
        importantAmenities = arrayListOf(),
        preferredContinents = arrayListOf()
    ),
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false
) 