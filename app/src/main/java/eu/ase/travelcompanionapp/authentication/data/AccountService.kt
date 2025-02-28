package eu.ase.travelcompanionapp.authentication.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import eu.ase.travelcompanionapp.authentication.domain.model.User
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import io.objectbox.Box
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountService(
    private val userBox: Box<UserEntity>
) : AccountRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                try {
                    val userEntity = userBox.all.firstOrNull { it.firebaseId == firebaseUser.uid }

                    if (userEntity == null) {
                        val newUser = UserEntity(
                            firebaseId = firebaseUser.uid,
                            email = firebaseUser.email.orEmpty()
                        )
                        userBox.put(newUser)
                        this.trySend(User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email.orEmpty()
                        ))
                    } else {
                        this.trySend(User(
                            id = userEntity.firebaseId,
                            email = userEntity.email,
                            name = userEntity.name,
                            birthDate = userEntity.birthDate,
                            gender = userEntity.gender,
                            phoneNumber = userEntity.phoneNumber
                        ))
                    }
                } catch (e: Exception) {
                    this.trySend(null)
                }
            } else {
                this.trySend(null)
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        awaitClose { Firebase.auth.removeAuthStateListener(listener) }
    }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()


    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email,password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun updateUserProfile(user: User) {
        try {
            val existingUser = userBox.all.firstOrNull { it.firebaseId == user.id }

            if (existingUser != null) {
                existingUser.apply {
                    email = user.email
                    name = user.name
                    birthDate = user.birthDate
                    gender = user.gender
                    phoneNumber = user.phoneNumber
                }
                userBox.put(existingUser)
            } else {
                val newUser = UserEntity(
                    firebaseId = user.id,
                    email = user.email,
                    name = user.name,
                    birthDate = user.birthDate,
                    gender = user.gender,
                    phoneNumber = user.phoneNumber
                )
                userBox.put(newUser)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAccount() {
        try {
            val userId = Firebase.auth.currentUser?.uid
            if (userId != null) {
                val userEntity = userBox.all.firstOrNull { it.firebaseId == userId }
                userEntity?.let { userBox.remove(it) }
            }
            Firebase.auth.currentUser!!.delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

}