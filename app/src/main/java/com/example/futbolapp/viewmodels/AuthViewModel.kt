package com.example.futbolapp // Asegúrate que el package sea el correcto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Necesario para viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // Necesario para asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // _currentUser se inicializa con el estado actual al crear el ViewModel.
    // El AuthStateListener lo mantendrá actualizado.
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Estados para UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // El listener que reacciona a los cambios de estado de autenticación
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (_currentUser.value?.uid != user?.uid) { // Solo actualiza si realmente cambió
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
            // El AuthStateListener se encargará de actualizar _currentUser.value a null.
            // No es estrictamente necesario asignar null aquí, pero no hace daño si quieres ser explícito
            // _currentUser.value = null
            Log.d("AuthViewModel", "Firebase auth.signOut() ejecutado.")
        }
    }

    fun signInWithEmailPassword(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                // El AuthStateListener actualizará currentUser, lo que activará la navegación en AppContent
                onSuccess() // Llama a onSuccess para cualquier lógica adicional post-login en la UI
                Log.d("AuthViewModel", "signInWithEmailPassword exitoso.")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en signInWithEmailPassword: ${e.message}", e)
                val errorMessage = e.message ?: "Error desconocido al iniciar sesión"
                _error.value = errorMessage
                onError(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUpWithEmailPassword(email: String, password: String, name: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    // Aquí podrías guardar datos adicionales del usuario en Firestore
                    Log.d("AuthViewModel", "signUpWithEmailPassword exitoso para ${user.uid}")
                    onSuccess()
                } else {
                    val errorMessage = "Error al crear la cuenta"
                    _error.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en signUpWithEmailPassword: ${e.message}", e)
                val errorMessage = e.message ?: "Error desconocido al registrarse"
                _error.value = errorMessage
                onError(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setError(error: String) {
        _error.value = error
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Es crucial remover el listener para evitar memory leaks y comportamiento inesperado
        auth.removeAuthStateListener(authStateListener)
        Log.d("AuthViewModel", "AuthViewModel onCleared y AuthStateListener removido.")
    }
}
