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

data class PatientsState(
    val isLoading: Boolean = false,
    val patients: List<Patient> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class PatientsViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {
    
    private val _patientsState = MutableStateFlow(PatientsState())
    val patientsState: StateFlow<PatientsState> = _patientsState.asStateFlow()
    
    init {
        loadPatients()
    }
    
    private fun loadPatients() {
        viewModelScope.launch {
            patientRepository.getPatients().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _patientsState.value = PatientsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _patientsState.value = PatientsState(patients = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _patientsState.value = PatientsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
}
