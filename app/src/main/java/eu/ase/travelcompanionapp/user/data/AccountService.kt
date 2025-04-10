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

    private val userCache = mutableMapOf<String, User>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = authRepository.isAuthenticated.flatMapLatest { isAuthenticated ->
        if (isAuthenticated) {
            val userId = authRepository.currentUserId
            val email = authRepository.getCurrentUserEmail() ?: ""

            flow {
                val cachedUser = userCache[userId]
                if (cachedUser != null) {
                    emit(cachedUser)
                }
                try {
                    when (val result = remoteUserRepository.fetchUser(userId)) {
                        is RemoteUserRepository.FetchUserResult.Success -> {
                            val remoteUser = result.user
                            userCache[userId] = remoteUser
                            if (cachedUser != remoteUser) {
                                emit(remoteUser)
                            }
                        }
                        is RemoteUserRepository.FetchUserResult.UserNotFound -> {
                            if (cachedUser == null) {
                                val newUser = User(id = userId, email = email)
                                val saveSuccess = remoteUserRepository.saveUser(newUser)
                                if (saveSuccess) {
                                    userCache[userId] = newUser
                                    emit(newUser)
                                }
                            }
                        }
                        is RemoteUserRepository.FetchUserResult.Error -> {
                            if (cachedUser == null) {
                                val basicUser = User(id = userId, email = email)
                                emit(basicUser)
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (cachedUser == null) {
                        emit(User(id = userId, email = email))
                    }
                }
            }
        } else {
            userCache.clear()
            flowOf(null)
        }
    }

    override suspend fun getUserById(userId: String): User? {
        val cachedUser = userCache[userId]
        if (cachedUser != null) {
            return cachedUser
        }

        return try {
            when (val result = remoteUserRepository.fetchUser(userId)) {
                is RemoteUserRepository.FetchUserResult.Success -> {
                    val user = result.user
                    userCache[userId] = user
                    user
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createUser(userId: String, email: String): User {
        val newUser = User(
            id = userId,
            email = email
        )

        val saveSuccess = remoteUserRepository.saveUser(newUser)
        if (saveSuccess) {
            userCache[userId] = newUser
        }

        return newUser
    }

    override suspend fun updateUserProfile(user: User) {
        try {
            val saveSuccess = remoteUserRepository.saveUser(user)
            if (saveSuccess) {
                userCache[user.id] = user
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUserData(userId: String) {
        try {
            remoteUserRepository.deleteUser(userId)
            userCache.remove(userId)
        } catch (e: Exception) {
            throw e
        }
    }
}