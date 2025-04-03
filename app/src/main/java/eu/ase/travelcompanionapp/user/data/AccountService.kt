package eu.ase.travelcompanionapp.user.data

import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.user.data.repository.RemoteUserRepository
import eu.ase.travelcompanionapp.user.domain.model.User
import eu.ase.travelcompanionapp.user.domain.repository.AccountRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class AccountService(
    private val authRepository: AuthRepository
) : AccountRepository {

    private val remoteUserRepository = RemoteUserRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = authRepository.isAuthenticated.flatMapLatest { isAuthenticated ->
        if (isAuthenticated) {
            val userId = authRepository.currentUserId

            flow {
                val firestoreUser = remoteUserRepository.fetchUser(userId)
                if (firestoreUser != null) {
                    emit(firestoreUser)
                } else {
                    emit(null)
                }
            }
        } else {
            flowOf(null)
        }
    }

    override suspend fun getUserById(userId: String): User? {
        val remoteUser = remoteUserRepository.fetchUser(userId)
        if (remoteUser != null) {
            return remoteUser
        }

        return null
    }

    override suspend fun createUser(userId: String, email: String): User {
        val newUser = User(
            id = userId,
            email = email
        )
        remoteUserRepository.saveUser(newUser)

        return newUser
    }

    override suspend fun updateUserProfile(user: User) {
        remoteUserRepository.saveUser(user)
    }

    override suspend fun deleteUserData(userId: String) {
        remoteUserRepository.deleteUser(userId)
    }
}