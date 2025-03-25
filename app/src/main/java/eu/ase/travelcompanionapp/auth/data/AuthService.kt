package eu.ase.travelcompanionapp.auth.data

import eu.ase.travelcompanionapp.auth.data.auth.AuthManager
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthService : AuthRepository {
    private val authManager = AuthManager()

    override val isAuthenticated: Flow<Boolean> = authManager.authStateFlow.map { it != null }

    override val currentUserId: String
        get() = authManager.currentUserId

    override fun hasUser(): Boolean = authManager.hasUser()

    override suspend fun signIn(email: String, password: String): String {
        val firebaseUser = authManager.signIn(email, password)
        return firebaseUser.uid
    }

    override suspend fun signUp(email: String, password: String): String {
        val firebaseUser = authManager.signUp(email, password)
        return firebaseUser.uid
    }

    override suspend fun signOut() {
        authManager.signOut()
    }

    override suspend fun deleteAccount() {
        authManager.deleteAccount()
    }
}