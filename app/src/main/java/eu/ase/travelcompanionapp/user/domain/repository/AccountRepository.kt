package eu.ase.travelcompanionapp.user.domain.repository

import eu.ase.travelcompanionapp.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentUser: Flow<User?>

    suspend fun getUserById(userId: String): User?
    suspend fun createUser(userId: String, email: String): User
    suspend fun updateUserProfile(user: User)
    suspend fun deleteUserData(userId: String)
}