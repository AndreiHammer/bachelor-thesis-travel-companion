package eu.ase.travelcompanionapp.recommendation.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.recommendation.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun createAndSendUserProfile(currentLocation: String? = null): Result<Unit, DataError>

    suspend fun getUserProfilePreview(): UserProfile

    suspend fun canCreateUserProfile(): Boolean

    suspend fun validateUserProfileData(): Result<Unit, DataError>
} 