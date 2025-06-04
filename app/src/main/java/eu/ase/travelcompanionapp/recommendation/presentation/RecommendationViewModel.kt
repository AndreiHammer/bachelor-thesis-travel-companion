package eu.ase.travelcompanionapp.recommendation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecommendationViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    init {
        checkQuestionnaireCompletion()
    }
    
    private fun checkQuestionnaireCompletion() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            userPreferencesRepository.hasCompletedQuestionnaire.collectLatest { hasCompleted ->
                _state.value = _state.value.copy(
                    hasCompletedQuestionnaire = hasCompleted,
                    isLoading = false
                )

                if (hasCompleted) {
                    _state.value = _state.value.copy(
                        showMessage = "Questionnaire completed! Destination recommendations will be implemented soon."
                    )
                }
            }
        }
    }

    fun onQuestionnaireCompleted() {
        checkQuestionnaireCompletion()
    }
}

data class RecommendationState(
    val hasCompletedQuestionnaire: Boolean = false,
    val isLoading: Boolean = false,
    val showMessage: String? = null
)