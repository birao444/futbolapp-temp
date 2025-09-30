package com.example.futbolapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.Permission
import com.example.futbolapp.RoleManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoleViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userRole = MutableStateFlow<String>(RoleManager.PENDIENTE)
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserRole()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    val role = userDoc.getString("role") ?: RoleManager.PENDIENTE
                    _userRole.value = role
                    Log.d("RoleViewModel", "Rol cargado: $role para usuario ${currentUser.uid}")
                } else {
                    _userRole.value = RoleManager.PENDIENTE
                }
            } catch (e: Exception) {
                Log.e("RoleViewModel", "Error cargando rol: ${e.message}", e)
                _userRole.value = RoleManager.PENDIENTE
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hasPermission(permission: Permission): Boolean {
        return RoleManager.hasPermission(_userRole.value, permission)
    }

    fun canAccessScreen(screenId: String): Boolean {
        return RoleManager.canAccessScreen(_userRole.value, screenId)
    }

    fun updateUserRole(newRole: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .update("role", newRole)
                        .await()

                    _userRole.value = newRole
                    Log.d("RoleViewModel", "Rol actualizado a: $newRole")
                    onSuccess()
                } else {
                    onError("Usuario no autenticado")
                }
            } catch (e: Exception) {
                Log.e("RoleViewModel", "Error actualizando rol: ${e.message}", e)
                onError("Error actualizando rol: ${e.message}")
            }
        }
    }

    fun assignRoleToUser(userId: String, teamId: String, role: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar permisos
                if (!RoleManager.canAssignRole(_userRole.value, role)) {
                    onError("No tienes permisos para asignar este rol")
                    return@launch
                }

                val roleData = mapOf(
                    "userId" to userId,
                    "teamId" to teamId,
                    "role" to role,
                    "assignedBy" to (auth.currentUser?.uid ?: ""),
                    "assignedAt" to System.currentTimeMillis()
                )

                firestore.collection("roles")
                    .document("$userId-$teamId")
                    .set(roleData)
                    .await()

                Log.d("RoleViewModel", "Rol asignado: $role a usuario $userId en equipo $teamId")
                onSuccess()
            } catch (e: Exception) {
                Log.e("RoleViewModel", "Error asignando rol: ${e.message}", e)
                onError("Error asignando rol: ${e.message}")
            }
        }
    }

    fun refreshRole() {
        loadUserRole()
    }
}
