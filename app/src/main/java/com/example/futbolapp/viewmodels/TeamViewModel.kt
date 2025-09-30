package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Team
import com.example.futbolapp.repositories.TeamRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamViewModel(
    private val teamRepository: TeamRepository = TeamRepository(),
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    private val _selectedTeam = MutableStateFlow<Team?>(null)
    val selectedTeam: StateFlow<Team?> = _selectedTeam

    private var teamsListener: ListenerRegistration? = null

    fun startListeningToTeamsForUser(userId: String) {
        // Remove previous listener if any
        teamsListener?.remove()
        // Start real-time listener
        teamsListener = firestoreManager.listenToTeamsForUser(userId) { dataList ->
            val teams = dataList.map {
                Team(
                    id = it["id"] as? String ?: "",
                    name = it["name"] as? String ?: "",
                    userId = it["userId"] as? String ?: "",
                    description = it["description"] as? String ?: "",
                    createdAt = it["createdAt"] as? Long ?: 0L
                )
            }
            _teams.value = teams
        }
    }

    fun selectTeam(team: Team) {
        _selectedTeam.value = team
    }

    fun createOrUpdateTeam(team: Team) {
        viewModelScope.launch {
            teamRepository.createTeam(team)
            // No need to reload, listener will update
        }
    }

    override fun onCleared() {
        super.onCleared()
        teamsListener?.remove()
    }
}
