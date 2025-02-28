package eu.ase.travelcompanionapp.authentication.domain.repository

import eu.ase.travelcompanionapp.authentication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(
        email: String,
        password: String
    )

    suspend fun signUp(
        email: String,
        password: String
    )

    suspend fun signOut()
    suspend fun deleteAccount()

    suspend fun updateUserProfile(
        user: User
    )
}