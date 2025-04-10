package eu.ase.travelcompanionapp.auth.domain


import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    val currentUserId: String
    fun getCurrentUserEmail(): String?

    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String): String  // Returns userId
    suspend fun signUp(email: String, password: String): String  // Returns userId
    suspend fun signOut()
    suspend fun deleteAccount()
}