package eu.ase.travelcompanionapp.authentication.data

import eu.ase.travelcompanionapp.authentication.data.auth.AuthManager
import eu.ase.travelcompanionapp.authentication.data.repository.RemoteUserRepository
import eu.ase.travelcompanionapp.authentication.data.repository.LocalUserRepository
import eu.ase.travelcompanionapp.authentication.domain.model.User
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import io.objectbox.Box
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class AccountService(
    userBox: Box<UserEntity>
) : AccountRepository {

    private val authManager = AuthManager()
    private val localUserRepository = LocalUserRepository(userBox)
    private val remoteUserRepository = RemoteUserRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = authManager.authStateFlow.flatMapConcat { firebaseUser ->
        if (firebaseUser != null) {
            val userEntity = localUserRepository.findUserByFirebaseId(firebaseUser.uid)
            if (userEntity != null) {
                flowOf(
                    with(localUserRepository) {
                        userEntity.toUser()
                    }
                )
            } else {
                flow {
                    val firestoreUser = remoteUserRepository.fetchUser(firebaseUser.uid)
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

    override val currentUserId: String
        get() = authManager.currentUserId


    override fun hasUser(): Boolean = authManager.hasUser()

    override suspend fun signIn(email: String, password: String) {
        val firebaseUser = authManager.signIn(email, password)

        val firestoreUser = remoteUserRepository.fetchUser(firebaseUser.uid)
        if (firestoreUser != null) {
            localUserRepository.saveUser(firestoreUser)
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            val firebaseUser = authManager.signUp(email, password)

            val newUser = User(
                id = firebaseUser.uid,
                email = firebaseUser.email.orEmpty()
            )

            localUserRepository.saveUser(newUser)
            remoteUserRepository.saveUser(newUser)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signOut() {
        authManager.signOut()
    }

    override suspend fun updateUserProfile(user: User) {
        localUserRepository.saveUser(user)

        remoteUserRepository.saveUser(user)
    }

    override suspend fun deleteAccount() {
        try {
            val userId = authManager.currentUserId
            if (userId.isNotEmpty()) {
                localUserRepository.deleteUser(userId)
                remoteUserRepository.deleteUser(userId)
                authManager.deleteAccount()
            }
        } catch (e: Exception) {
            throw e
        }

    }

}