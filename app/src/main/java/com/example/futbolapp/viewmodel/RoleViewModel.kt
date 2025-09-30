package com.example.futbolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.firebase.RoleManager
import com.example.futbolapp.models.Permission
import com.example.futbolapp.models.TeamMember
import com.example.futbolapp.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoleViewModel : ViewModel() {
    private val roleManager = RoleManager()

    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    /**
     * Cargar todos los miembros de un equipo
     */
    fun loadTeamMembers(teamId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val members = roleManager.getTeamMembers(teamId)
                _teamMembers.value = members
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar miembros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar el rol del usuario actual
     */
    fun loadCurrentUserRole(userId: String, teamId: String) {
        viewModelScope.launch {
            try {
                val role = roleManager.getUserRole(userId, teamId)
                _currentUserRole.value = role
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar rol: ${e.message}"
            }
        }
    }

    /**
     * Asignar un rol a un usuario
     */
    fun assignRole(
        userId: String,
        teamId: String,
        role: UserRole,
        name: String,
        email: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = roleManager.assignRole(userId, teamId, role, name, email)
                if (result.isSuccess) {
                    _successMessage.value = "Rol asignado correctamente"
                    loadTeamMembers(teamId) // Recargar la lista
                } else {
                    _errorMessage.value = "Error al asignar rol: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cambiar el rol de un usuario
     */
    fun updateRole(userId: String, teamId: String, newRole: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = roleManager.updateRole(userId, teamId, newRole)
                if (result.isSuccess) {
                    _successMessage.value = "Rol actualizado correctamente"
                    loadTeamMembers(teamId) // Recargar la lista
                } else {
                    _errorMessage.value = "Error al actualizar rol: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Eliminar un miembro del equipo
     */
    fun removeMember(userId: String, teamId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = roleManager.removeMember(userId, teamId)
                if (result.isSuccess) {
                    _successMessage.value = "Miembro eliminado correctamente"
                    loadTeamMembers(teamId) // Recargar la lista
                } else {
                    _errorMessage.value = "Error al eliminar miembro: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Verificar si el usuario actual tiene un permiso específico
     */
    suspend fun hasPermission(userId: String, teamId: String, permission: Permission): Boolean {
        return roleManager.hasPermission(userId, teamId, permission)
    }

    /**
     * Obtener miembros por rol
     */
    fun loadMembersByRole(teamId: String, role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val members = roleManager.getMembersByRole(teamId, role)
                _teamMembers.value = members
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar miembros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpiar mensajes
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
