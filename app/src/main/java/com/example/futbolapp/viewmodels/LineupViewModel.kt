package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Lineup
import com.example.futbolapp.repositories.LineupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LineupViewModel(
    private val lineupRepository: LineupRepository = LineupRepository()
) : ViewModel() {

    private val _lineups = MutableStateFlow<List<Lineup>>(emptyList())
    val lineups: StateFlow<List<Lineup>> = _lineups

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadLineupsForMatch(matchId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val lineupsList = lineupRepository.getLineupForMatch(matchId)
                _lineups.value = lineupsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading lineups"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createLineup(lineup: Lineup, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val lineupId = lineupRepository.createLineup(lineup)
                onSuccess(lineupId)
                // Reload lineups after creating
                loadLineupsForMatch(lineup.matchId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating lineup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLineup(lineupId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                lineupRepository.updateLineup(lineupId, updates)
                onSuccess()
                // Reload lineups
                _lineups.value.firstOrNull()?.let { loadLineupsForMatch(it.matchId) }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating lineup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteLineup(lineupId: String, matchId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                lineupRepository.deleteLineup(lineupId)
                onSuccess()
                // Reload lineups after deleting
                loadLineupsForMatch(matchId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error deleting lineup"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
