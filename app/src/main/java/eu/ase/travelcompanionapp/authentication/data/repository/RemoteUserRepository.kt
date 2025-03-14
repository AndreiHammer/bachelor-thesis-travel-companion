package eu.ase.travelcompanionapp.authentication.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import eu.ase.travelcompanionapp.authentication.domain.model.User
import kotlinx.coroutines.tasks.await

class RemoteUserRepository {
    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    suspend fun fetchUser(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                User(
                    id = userId,
                    email = document.getString("email") ?: "",
                    name = document.getString("name") ?: "",
                    birthDate = document.getString("birthDate") ?: "",
                    gender = document.getString("gender") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUser(user: User) {
        val userData = hashMapOf(
            "email" to user.email,
            "name" to user.name,
            "birthDate" to user.birthDate,
            "gender" to user.gender,
            "phoneNumber" to user.phoneNumber
        )
        usersCollection.document(user.id).set(userData).await()
    }

    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }
}