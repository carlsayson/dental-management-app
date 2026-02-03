package com.dentalcare.app.ui.screens.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.model.Patient
import com.dentalcare.app.data.repository.PatientRepository
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaveState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {
    
    private val _saveState = MutableStateFlow(SaveState())
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()
    
    private val _currentPatient = MutableStateFlow<Patient?>(null)
    val currentPatient: StateFlow<Patient?> = _currentPatient.asStateFlow()
    
    private val _deleteState = MutableStateFlow(SaveState())
    val deleteState: StateFlow<SaveState> = _deleteState.asStateFlow()
    
    fun loadPatient(patientId: String) {
        viewModelScope.launch {
            patientRepository.getPatient(patientId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _currentPatient.value = result.data
                    }
                    is Resource.Error -> {
                        _saveState.value = SaveState(error = result.message ?: "Failed to load patient")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
    
    fun savePatient(
        id: String,
        firstName: String,
        lastName: String,
        phone: String,
        address: String,
        medicalHistory: String,
        dentalHistory: String
    ) {
        viewModelScope.launch {
            val patient = Patient(
                id = id,
                firstName = firstName,
                lastName = lastName,
                name = "$firstName $lastName",
                phone = phone,
                address = address,
                medicalHistory = medicalHistory,
                dentalHistory = dentalHistory
            )
            
            val flow = if (id.isEmpty()) {
                patientRepository.addPatient(patient)
            } else {
                patientRepository.updatePatient(patient)
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
                        _saveState.value = SaveState(error = result.message ?: "Failed to save patient")
                    }
                }
            }
        }
    }
    
    fun deletePatient(patientId: String) {
        viewModelScope.launch {
            patientRepository.deletePatient(patientId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _deleteState.value = SaveState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _deleteState.value = SaveState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _deleteState.value = SaveState(error = result.message ?: "Failed to delete patient")
                    }
                }
            }
        }
    }
}
