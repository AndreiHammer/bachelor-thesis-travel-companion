package eu.ase.travelcompanionapp.user.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import eu.ase.travelcompanionapp.user.domain.model.User
import kotlinx.coroutines.tasks.await

class RemoteUserRepository {
    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    sealed class FetchUserResult {
        data class Success(val user: User) : FetchUserResult()
        data object UserNotFound : FetchUserResult()
        data class Error(val exception: Exception) : FetchUserResult()
    }

    suspend fun fetchUser(userId: String): FetchUserResult {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = User(
                    id = userId,
                    email = document.getString("email") ?: "",
                    name = document.getString("name") ?: "",
                    birthDate = document.getString("birthDate") ?: "",
                    gender = document.getString("gender") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: ""
                )
                FetchUserResult.Success(user)
            } else {
                FetchUserResult.UserNotFound
            }
        } catch (e: FirebaseFirestoreException) {
            FetchUserResult.Error(e)
        } catch (e: Exception) {
            FetchUserResult.Error(e)
        }
    }


    suspend fun saveUser(user: User): Boolean {
        return try {
            val userData = hashMapOf(
                "email" to user.email,
                "name" to user.name,
                "birthDate" to user.birthDate,
                "gender" to user.gender,
                "phoneNumber" to user.phoneNumber
            )
            usersCollection.document(user.id).set(userData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(userId: String): Boolean {
        return try {
            usersCollection.document(userId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}