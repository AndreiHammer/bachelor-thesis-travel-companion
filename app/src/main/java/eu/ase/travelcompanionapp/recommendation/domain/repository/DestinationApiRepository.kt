package eu.ase.travelcompanionapp.recommendation.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.recommendation.domain.model.UserProfile

interface DestinationApiRepository {
    suspend fun createUserProfile(
        userProfile: UserProfile
    ): Result<Unit, DataError>
}