package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Player
import com.example.futbolapp.repositories.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository = PlayerRepository()
) : ViewModel() {

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPlayersForTeam(teamId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val playersList = playerRepository.getPlayersForTeam(teamId)
                _players.value = playersList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading players"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPlayer(player: Player, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val playerId = playerRepository.createPlayer(player)
                onSuccess(playerId)
                // Reload players after creating
                loadPlayersForTeam(player.teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating player"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePlayer(playerId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                playerRepository.updatePlayer(playerId, updates)
                onSuccess()
                // Reload players
                _players.value.firstOrNull()?.let { loadPlayersForTeam(it.teamId) }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating player"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePlayer(playerId: String, teamId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                playerRepository.deletePlayer(playerId)
                onSuccess()
                // Reload players after deleting
                loadPlayersForTeam(teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error deleting player"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
