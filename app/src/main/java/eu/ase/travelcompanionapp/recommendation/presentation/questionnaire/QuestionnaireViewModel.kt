package eu.ase.travelcompanionapp.recommendation.presentation.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        // Reset completion state when initializing
        _state.value = _state.value.copy(
            isEditMode = isEditMode,
            isCompleted = false,
            errorMessage = null
        )
        
        if (isEditMode) {
            loadExistingPreferences()
        }
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

    fun updatePreferredActivities(activities: List<String>) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(preferredActivities = ArrayList(activities))
        )
    }

    fun updateClimatePreference(climate: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(climatePreference = climate)
        )
    }

    fun updateTravelStyle(style: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(travelStyle = style)
        )
    }

    fun updateTripDuration(duration: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(tripDuration = duration)
        )
    }

    fun updateCompanions(companions: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(companions = companions)
        )
    }

    fun updateCulturalOpenness(openness: Int) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(culturalOpenness = openness)
        )
    }

    fun updatePreferredCountry(country: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(preferredCountry = country)
        )
    }

    fun updateBucketListThemes(themes: List<String>) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(bucketListThemes = ArrayList(themes))
        )
    }

    fun updateBudgetRange(budget: String) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(budgetRange = budget)
        )
    }

    fun updatePreferredContinents(continents: List<String>) {
        _state.value = _state.value.copy(
            preferences = _state.value.preferences.copy(preferredContinents = ArrayList(continents))
        )
    }

    fun savePreferences() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.saveQuestionnaireResponse(_state.value.preferences)
                _state.value = _state.value.copy(isCompleted = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }
}

data class QuestionnaireState(
    val preferences: QuestionnaireResponse = QuestionnaireResponse(),
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false
) 