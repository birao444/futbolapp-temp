package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Improvement
import com.example.futbolapp.repositories.ImprovementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImprovementViewModel(
    private val improvementRepository: ImprovementRepository = ImprovementRepository()
) : ViewModel() {

    private val _improvements = MutableStateFlow<List<Improvement>>(emptyList())
    val improvements: StateFlow<List<Improvement>> = _improvements

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadImprovementsForTeam(teamId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val improvementsList = improvementRepository.getImprovementsForTeam(teamId)
                _improvements.value = improvementsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading improvements"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createImprovement(improvement: Improvement, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val improvementId = improvementRepository.createImprovement(improvement)
                onSuccess(improvementId)
                // Reload improvements after creating
                loadImprovementsForTeam(improvement.teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating improvement"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateImprovement(improvementId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                improvementRepository.updateImprovement(improvementId, updates)
                onSuccess()
                // Reload improvements
                _improvements.value.firstOrNull()?.let { loadImprovementsForTeam(it.teamId) }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating improvement"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeImprovement(improvementId: String, teamId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                improvementRepository.completeImprovement(improvementId)
                onSuccess()
                // Reload improvements after completing
                loadImprovementsForTeam(teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error completing improvement"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
