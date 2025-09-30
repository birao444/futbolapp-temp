package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.firebase.FirebaseAuthManager
import com.example.futbolapp.firebase.FirebaseFirestoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authManager: FirebaseAuthManager = FirebaseAuthManager(),
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val currentUser = authManager.currentUser

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = authManager.signIn(email, password)
                if (user != null) {
                    onSuccess()
                } else {
                    _error.value = "Error al iniciar sesión"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = authManager.signUp(email, password)
                if (user != null) {
                    // Create user data in Firestore
                    val userData = mapOf(
                        "id" to user.uid,
                        "email" to email,
                        "name" to name,
                        "role" to "jugador"
                    )
                    firestoreManager.createOrUpdateUserData(user.uid, userData)
                    onSuccess()
                } else {
                    _error.value = "Error al registrarse"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al registrarse"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun setError(error: String) {
        _error.value = error
    }

    fun clearError() {
        _error.value = null
    }
}
