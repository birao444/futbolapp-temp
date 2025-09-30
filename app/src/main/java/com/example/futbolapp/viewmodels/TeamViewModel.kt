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

    private val _selectedTeam = MutableStateFlow<Team?>(null)
    val selectedTeam: StateFlow<Team?> = _selectedTeam

    fun loadTeamsForUser(userId: String) {
        viewModelScope.launch {
            val loadedTeams = teamRepository.getTeamsForUser(userId)
            _teams.value = loadedTeams
        }
    }

    fun selectTeam(team: Team) {
        _selectedTeam.value = team
    }

    fun createOrUpdateTeam(team: Team) {
        viewModelScope.launch {
            teamRepository.createTeam(team)
            loadTeamsForUser(team.userId)
        }
    }
}
