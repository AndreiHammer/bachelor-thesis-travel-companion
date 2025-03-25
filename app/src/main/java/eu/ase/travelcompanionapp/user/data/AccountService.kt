package eu.ase.travelcompanionapp.user.data

import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.user.data.repository.RemoteUserRepository
import eu.ase.travelcompanionapp.user.data.repository.LocalUserRepository
import eu.ase.travelcompanionapp.user.domain.model.User
import eu.ase.travelcompanionapp.user.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import io.objectbox.Box
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class AccountService(
    userBox: Box<UserEntity>,
    private val authRepository: AuthRepository
) : AccountRepository {

    private val localUserRepository = LocalUserRepository(userBox)
    private val remoteUserRepository = RemoteUserRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = authRepository.isAuthenticated.flatMapLatest { isAuthenticated ->
        if (isAuthenticated) {
            val userId = authRepository.currentUserId
            val userEntity = localUserRepository.findUserByFirebaseId(userId)

            if (userEntity != null) {
                flowOf(with(localUserRepository) { userEntity.toUser() })
            } else {
                flow {
                    val firestoreUser = remoteUserRepository.fetchUser(userId)
                    if (firestoreUser != null) {
                        localUserRepository.saveUser(firestoreUser)
                        emit(firestoreUser)
                    } else {
                        emit(null)
                    }
                }
            }
        } else {
            flowOf(null)
        }
    }

    override suspend fun getUserById(userId: String): User? {
        val localUser = localUserRepository.findUserByFirebaseId(userId)
        if (localUser != null) {
            return with(localUserRepository) { localUser.toUser() }
        }

        val remoteUser = remoteUserRepository.fetchUser(userId)
        if (remoteUser != null) {
            localUserRepository.saveUser(remoteUser)
            return remoteUser
        }

        return null
    }

    override suspend fun createUser(userId: String, email: String): User {
        val newUser = User(
            id = userId,
            email = email
        )

        localUserRepository.saveUser(newUser)
        remoteUserRepository.saveUser(newUser)

        return newUser
    }

    override suspend fun updateUserProfile(user: User) {
        localUserRepository.saveUser(user)
        remoteUserRepository.saveUser(user)
    }

    override suspend fun deleteUserData(userId: String) {
        localUserRepository.deleteUser(userId)
        remoteUserRepository.deleteUser(userId)
    }
}