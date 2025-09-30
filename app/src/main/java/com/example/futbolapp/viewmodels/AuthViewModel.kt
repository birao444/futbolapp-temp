package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signUp(email, password, name)
                if (user != null) {
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Sign up failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signIn(email, password)
                if (user != null) {
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
