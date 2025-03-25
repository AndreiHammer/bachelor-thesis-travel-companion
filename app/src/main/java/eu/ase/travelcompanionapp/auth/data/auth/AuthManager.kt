package eu.ase.travelcompanionapp.auth.data.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth = Firebase.auth

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            this.trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    fun hasUser(): Boolean {
        return auth.currentUser != null
    }

    suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw IllegalStateException("Failed to sign in")
    }

    suspend fun signUp(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw IllegalStateException("Failed to sign up")
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount() {
        try {
            auth.currentUser?.delete()?.await()
        } catch (e: Exception) {
            throw e
        }
    }

}