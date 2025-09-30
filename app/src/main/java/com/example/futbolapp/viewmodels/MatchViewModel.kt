package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Match
import com.example.futbolapp.repositories.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MatchViewModel(
    private val matchRepository: MatchRepository = MatchRepository()
) : ViewModel() {

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMatchesForTeam(teamId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val matchesList = matchRepository.getMatchesForTeam(teamId)
                _matches.value = matchesList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading matches"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createMatch(match: Match, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val matchId = matchRepository.createMatch(match)
                onSuccess(matchId)
                // Reload matches after creating
                loadMatchesForTeam(match.homeTeamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating match"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMatch(matchId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                matchRepository.updateMatch(matchId, updates)
                onSuccess()
                // Reload matches
                _matches.value.firstOrNull()?.let { loadMatchesForTeam(it.homeTeamId) }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating match"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun listenToUpcomingMatches(teamId: String) {
        matchRepository.listenToUpcomingMatches(teamId)
    }

    fun stopListening() {
        matchRepository.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
