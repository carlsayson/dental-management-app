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
}
