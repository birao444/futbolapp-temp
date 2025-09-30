package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Statistics
import com.example.futbolapp.repositories.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val statisticsRepository: StatisticsRepository = StatisticsRepository()
) : ViewModel() {

    private val _statistics = MutableStateFlow<List<Statistics>>(emptyList())
    val statistics: StateFlow<List<Statistics>> = _statistics

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStatisticsForPlayer(playerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val statsList = statisticsRepository.getStatisticsForPlayer(playerId)
                _statistics.value = statsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading statistics"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStatisticsForMatch(matchId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val statsList = statisticsRepository.getStatisticsForMatch(matchId)
                _statistics.value = statsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading match statistics"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createStatistics(stats: Statistics, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val statsId = statisticsRepository.createStatistics(stats)
                onSuccess(statsId)
                // Reload statistics after creating
                loadStatisticsForPlayer(stats.playerId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating statistics"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStatistics(statsId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                statisticsRepository.updateStatistics(statsId, updates)
                onSuccess()
                // Reload statistics
                _statistics.value.firstOrNull()?.let { loadStatisticsForPlayer(it.playerId) }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating statistics"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
