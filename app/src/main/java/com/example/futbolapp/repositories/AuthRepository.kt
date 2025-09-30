package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseAuthManager
import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.User
import com.google.firebase.auth.FirebaseUser

class AuthRepository(
    private val authManager: FirebaseAuthManager = FirebaseAuthManager(),
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    val currentUser: FirebaseUser?
        get() = authManager.currentUser

    suspend fun signUp(email: String, password: String, name: String): FirebaseUser? {
        val user = authManager.signUp(email, password)
        user?.let {
            val userData = mapOf(
                "email" to email,
                "name" to name,
                "role" to "jugador"
            )
            firestoreManager.createOrUpdateUserData(it.uid, userData)
        }
        return user
    }

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return authManager.signIn(email, password)
    }

    fun signOut() {
        authManager.signOut()
    }

    suspend fun getUserData(userId: String): User? {
        val data = firestoreManager.getUserData(userId)
        return data?.let {
            User(
                id = userId,
                email = it["email"] as? String ?: "",
                name = it["name"] as? String ?: "",
                role = it["role"] as? String ?: "jugador"
            )
        }
    }
}
