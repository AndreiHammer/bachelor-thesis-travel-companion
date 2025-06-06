package eu.ase.travelcompanionapp.recommendation.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import eu.ase.travelcompanionapp.app.navigation.routes.DestinationRoute
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.recommendation.domain.model.RecommendedDestinations
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationApiRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecommendationViewModel(
    private val navController: NavController,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userProfileRepository: UserProfileRepository,
    private val destinationApiRepository: DestinationApiRepository
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
                            isLoadingRecommendations = true,
                            apiResult = "✅ Success! Your profile data was sent to the API successfully.",
                            errorMessage = null
                        )
                        getRecommendationsInternal()
                    }
                    is Result.Error -> {
                        val errorMessage = when (result.error) {
                            DataError.Local.QUESTIONNAIRE_NOT_COMPLETED ->
                                "Please complete the questionnaire first."
                            DataError.Remote.NO_INTERNET ->
                                "No internet connection. Please check your network."
                            DataError.Remote.SERVER ->
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
    
    fun getRecommendations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingRecommendations = true,
                recommendationsError = null
            )
            
            getRecommendationsInternal()
        }
    }
    
    private suspend fun getRecommendationsInternal() {
        try {
            val userProfile = userProfileRepository.getUserProfilePreview()
            
            when (val result = destinationApiRepository.getRecommendedDestinations(userProfile.userId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoadingRecommendations = false,
                        recommendations = result.data,
                        recommendationsError = null
                    )
                }
                is Result.Error -> {
                    val errorMessage = when (result.error) {
                        DataError.Remote.NO_INTERNET ->
                            "No internet connection. Please check your network."
                        DataError.Remote.REQUEST_TIMEOUT ->
                            "Request timed out. The AI is working hard to generate your recommendations. Please try again."
                        DataError.Remote.SERVER ->
                            "Server error. Please try again later."
                        DataError.Remote.SERIALIZATION ->
                            "Data parsing error. Please try again."
                        DataError.Remote.UNKNOWN ->
                            "Network error occurred. The server might be processing your request. Please try again."
                        else -> "Failed to get recommendations: ${result.error}"
                    }
                    
                    _state.value = _state.value.copy(
                        isLoadingRecommendations = false,
                        recommendationsError = errorMessage
                    )
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoadingRecommendations = false,
                recommendationsError = "Unexpected error: ${e.message}"
            )
        }
    }
    
    fun clearMessages() {
        _state.value = _state.value.copy(
            apiResult = null,
            errorMessage = null,
            profilePreview = null,
            recommendationsError = null
        )
    }

    fun navigateToDestinationList() {
        navController.navigate(DestinationRoute.DestinationList)
    }
}

data class RecommendationState(
    val hasCompletedQuestionnaire: Boolean = false,
    val isLoading: Boolean = false,
    val isSendingProfile: Boolean = false,
    val showMessage: String? = null,
    val apiResult: String? = null,
    val errorMessage: String? = null,
    val profilePreview: String? = null,
    val isLoadingRecommendations: Boolean = false,
    val recommendations: RecommendedDestinations? = null,
    val recommendationsError: String? = null
) 