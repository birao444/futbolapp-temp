package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Team
import com.example.futbolapp.repositories.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamViewModel(
    private val teamRepository: TeamRepository = TeamRepository()
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    private val _currentTeam = MutableStateFlow<Team?>(null)
    val currentTeam: StateFlow<Team?> = _currentTeam

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        _currentTeam.value = teamRepository.currentTeam.value
    }

    fun loadTeamsForUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val teamsList = teamRepository.getTeamsForUser(userId)
                _teams.value = teamsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading teams"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTeam(team: Team, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val teamId = teamRepository.createTeam(team)
                onSuccess(teamId)
                // Reload teams
                loadTeamsForUser(team.userId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating team"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTeam(teamId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                teamRepository.updateTeam(teamId, updates)
                onSuccess()
                // Reload current team if it's the one updated
                if (_currentTeam.value?.id == teamId) {
                    listenToTeam(teamId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating team"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun listenToTeam(teamId: String) {
        teamRepository.listenToTeam(teamId)
    }

    fun startListeningToTeamsForUser(userId: String) {
        teamRepository.listenToTeamsForUser(userId) { teamsData ->
            val teams = teamsData.mapNotNull { data ->
                Team(
                    id = data["id"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    createdAt = data["createdAt"] as? Long ?: 0L
                )
            }
            _teams.value = teams
        }
    }

    fun stopListening() {
        teamRepository.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
