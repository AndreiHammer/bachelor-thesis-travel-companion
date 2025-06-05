package eu.ase.travelcompanionapp.recommendation.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecommendationViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userProfileRepository: UserProfileRepository
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
                        showMessage = "Questionnaire completed! You can now send your profile data to get personalized recommendations."
                    )
                }
            }
        }
    }

    fun onQuestionnaireCompleted() {
        checkQuestionnaireCompletion()
    }
    
    fun sendUserProfileToApi() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSendingProfile = true,
                apiResult = null,
                errorMessage = null
            )
            
            try {
                when (val result = userProfileRepository.createAndSendUserProfile()) {
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            isSendingProfile = false,
                            apiResult = "✅ Success! Your profile data was sent to the API successfully.",
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError.Local.QUESTIONNAIRE_NOT_COMPLETED -> 
                                "Please complete the questionnaire first."
                            eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError.Remote.NO_INTERNET -> 
                                "No internet connection. Please check your network."
                            eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError.Remote.SERVER -> 
                                "Server error. Please try again later."
                            else -> "Failed to send profile data: ${result.error}"
                        }
                        
                        _state.value = _state.value.copy(
                            isSendingProfile = false,
                            apiResult = null,
                            errorMessage = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSendingProfile = false,
                    apiResult = null,
                    errorMessage = "Unexpected error: ${e.message}"
                )
            }
        }
    }
    
    fun getProfilePreview() {
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.getUserProfilePreview()
                _state.value = _state.value.copy(
                    profilePreview = "Profile Preview:\n" +
                            "User ID: ${profile.userId}\n" +
                            "Budget: ${profile.preferences.budgetRange}\n" +
                            "Purpose: ${profile.preferences.travelPurpose}\n" +
                            "Saved Hotels: ${profile.savedHotels.size}\n" +
                            "Booking History: ${profile.bookedOffers.size}\n" +
                            "Visited Destinations: ${profile.visitedDestinations.size}\n" +
                            "Current Location: ${profile.currentLocation ?: "Not set"}\n",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed to get profile preview: ${e.message}",
                    profilePreview = null
                )
            }
        }
    }
    
    fun clearMessages() {
        _state.value = _state.value.copy(
            apiResult = null,
            errorMessage = null,
            profilePreview = null
        )
    }
}

data class RecommendationState(
    val hasCompletedQuestionnaire: Boolean = false,
    val isLoading: Boolean = false,
    val isSendingProfile: Boolean = false,
    val showMessage: String? = null,
    val apiResult: String? = null,
    val errorMessage: String? = null,
    val profilePreview: String? = null
)