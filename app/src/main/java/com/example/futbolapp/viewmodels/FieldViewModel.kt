package com.example.futbolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolapp.models.Field
import com.example.futbolapp.repositories.FieldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FieldViewModel(
    private val fieldRepository: FieldRepository = FieldRepository()
) : ViewModel() {

    private val _fields = MutableStateFlow<List<Field>>(emptyList())
    val fields: StateFlow<List<Field>> = _fields

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllFields() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fieldsList = fieldRepository.getAllFields()
                _fields.value = fieldsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading fields"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createField(field: Field, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fieldId = fieldRepository.createField(field)
                onSuccess(fieldId)
                // Reload fields after creating
                loadAllFields()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error creating field"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateField(fieldId: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                fieldRepository.updateField(fieldId, updates)
                onSuccess()
                // Reload fields
                loadAllFields()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating field"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteField(fieldId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                fieldRepository.deleteField(fieldId)
                onSuccess()
                // Reload fields after deleting
                loadAllFields()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error deleting field"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
