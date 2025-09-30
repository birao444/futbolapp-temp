package com.example.futbolapp.viewmodels // Asegúrate que el package sea el correcto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // IMPORTANTE PARA .await()

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (_currentUser.value?.uid != user?.uid) {
            _currentUser.value = user
            Log.d("AuthViewModel", "AuthStateListener: Usuario cambiado a: ${user?.uid ?: "null"}")
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        Log.d("AuthViewModel", "AuthViewModel inicializado. Usuario actual: ${_currentUser.value?.uid ?: "null"}")
    }

    fun signOut() {
        Log.d("AuthViewModel", "signOut() llamado.")
        viewModelScope.launch {
            auth.signOut()
            Log.d("AuthViewModel", "Firebase auth.signOut() ejecutado.")
        }
    }

    // Ejemplo de funciones signIn y signUp
    fun signIn(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, pass).await() // USA .await()
                // El AuthStateListener actualizará currentUser
                onSuccess()
                Log.d("AuthViewModel", "signIn exitoso.")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en signIn: ${e.message}", e)
                onError(e.message ?: "Error desconocido al iniciar sesión")
            }
        }
    }

    fun signUp(email: String, pass: String, name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, pass).await() // USA .await()
                val user = authResult.user
                if (user != null) {
                    // Crear perfil de usuario en Firestore
                    createUserProfile(user, name)
                    Log.d("AuthViewModel", "Usuario creado con UID: ${user.uid}, Nombre: $name")
                }
                // El AuthStateListener actualizará currentUser
                onSuccess()
                Log.d("AuthViewModel", "signUp exitoso.")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en signUp: ${e.message}", e)
                onError(e.message ?: "Error desconocido al registrar")
            }
        }
    }

    // Función para crear perfil de usuario en Firestore
    private suspend fun createUserProfile(user: FirebaseUser, name: String) {
        try {
            val userData = hashMapOf(
                "name" to name,
                "email" to user.email,
                "role" to "pendiente", // Rol inicial
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(user.uid).set(userData).await()
            Log.d("AuthViewModel", "Perfil de usuario creado en Firestore para UID: ${user.uid}")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error creando perfil de usuario: ${e.message}", e)
            throw e // Re-lanzar para que signUp lo maneje
        }
    }


    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
        Log.d("AuthViewModel", "AuthViewModel onCleared y AuthStateListener removido.")
    }
}
