package com.dentalcare.app.ui.screens.treatments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.model.Treatment
import com.dentalcare.app.data.repository.TreatmentRepository
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TreatmentsState(
    val isLoading: Boolean = false,
    val treatments: List<Treatment> = emptyList(),
    val error: String = ""
)

data class SaveState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class TreatmentsViewModel @Inject constructor(
    private val treatmentRepository: TreatmentRepository
) : ViewModel() {
    
    private val _treatmentsState = MutableStateFlow(TreatmentsState())
    val treatmentsState: StateFlow<TreatmentsState> = _treatmentsState.asStateFlow()
    
    private val _saveState = MutableStateFlow(SaveState())
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()
    
    private val _currentTreatment = MutableStateFlow<Treatment?>(null)
    val currentTreatment: StateFlow<Treatment?> = _currentTreatment.asStateFlow()
    
    init {
        loadTreatments()
    }
    
    private fun loadTreatments() {
        viewModelScope.launch {
            treatmentRepository.getTreatments().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _treatmentsState.value = TreatmentsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _treatmentsState.value = TreatmentsState(treatments = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _treatmentsState.value = TreatmentsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
    
    fun loadTreatmentsByPatient(patientId: String) {
        viewModelScope.launch {
            treatmentRepository.getTreatmentsByPatient(patientId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _treatmentsState.value = TreatmentsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _treatmentsState.value = TreatmentsState(treatments = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _treatmentsState.value = TreatmentsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
    
    fun loadTreatment(treatmentId: String) {
        viewModelScope.launch {
            treatmentRepository.getTreatment(treatmentId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _currentTreatment.value = result.data
                    }
                    is Resource.Error -> {
                        _saveState.value = SaveState(error = result.message ?: "Failed to load treatment")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
    
    fun saveTreatment(treatment: Treatment) {
        viewModelScope.launch {
            val flow = if (treatment.id.isEmpty()) {
                treatmentRepository.addTreatment(treatment)
            } else {
                treatmentRepository.updateTreatment(treatment)
            }
            
            flow.collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _saveState.value = SaveState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _saveState.value = SaveState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _saveState.value = SaveState(error = result.message ?: "Failed to save treatment")
                    }
                }
            }
        }
    }
    
    fun deleteTreatment(treatmentId: String) {
        viewModelScope.launch {
            treatmentRepository.deleteTreatment(treatmentId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _saveState.value = SaveState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _saveState.value = SaveState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _saveState.value = SaveState(error = result.message ?: "Failed to delete treatment")
                    }
                }
            }
        }
    }
}
