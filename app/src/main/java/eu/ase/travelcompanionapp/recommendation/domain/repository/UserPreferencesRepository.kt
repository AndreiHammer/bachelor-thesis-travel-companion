package eu.ase.travelcompanionapp.recommendation.domain.repository

import eu.ase.travelcompanionapp.recommendation.domain.model.QuestionnaireResponse
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val hasCompletedQuestionnaire: Flow<Boolean>
    val questionnaireResponse: Flow<QuestionnaireResponse?>
    
    suspend fun saveQuestionnaireResponse(response: QuestionnaireResponse)
    suspend fun clearQuestionnaireResponse()
    suspend fun hasQuestionnaire(): Boolean
} 