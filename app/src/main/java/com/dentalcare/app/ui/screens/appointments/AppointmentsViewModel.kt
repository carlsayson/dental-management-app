package com.dentalcare.app.ui.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.model.Appointment
import com.dentalcare.app.data.repository.AppointmentRepository
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppointmentsState(
    val isLoading: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {
    
    private val _appointmentsState = MutableStateFlow(AppointmentsState())
    val appointmentsState: StateFlow<AppointmentsState> = _appointmentsState.asStateFlow()
    
    private val _saveState = MutableStateFlow(SaveState())
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()
    
    init {
        loadAppointments()
    }
    
    private fun loadAppointments() {
        viewModelScope.launch {
            appointmentRepository.getAppointments().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _appointmentsState.value = AppointmentsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _appointmentsState.value = AppointmentsState(appointments = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _appointmentsState.value = AppointmentsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
    
    fun loadAppointmentsByPatient(patientId: String) {
        viewModelScope.launch {
            appointmentRepository.getAppointmentsByPatient(patientId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _appointmentsState.value = AppointmentsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _appointmentsState.value = AppointmentsState(appointments = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _appointmentsState.value = AppointmentsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
    
    fun saveAppointment(appointment: Appointment) {
        viewModelScope.launch {
            val flow = if (appointment.id.isEmpty()) {
                appointmentRepository.addAppointment(appointment)
            } else {
                appointmentRepository.updateAppointment(appointment)
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
                        _saveState.value = SaveState(error = result.message ?: "Failed to save appointment")
                    }
                }
            }
        }
    }
    
    fun deleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            appointmentRepository.deleteAppointment(appointmentId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _saveState.value = SaveState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _saveState.value = SaveState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _saveState.value = SaveState(error = result.message ?: "Failed to delete appointment")
                    }
                }
            }
        }
    }
}

data class SaveState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)

