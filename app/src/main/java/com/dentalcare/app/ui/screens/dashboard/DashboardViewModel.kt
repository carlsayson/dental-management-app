package com.dentalcare.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.model.Appointment
import com.dentalcare.app.data.model.User
import com.dentalcare.app.data.repository.AppointmentRepository
import com.dentalcare.app.data.repository.AuthRepository
import com.dentalcare.app.data.repository.PatientRepository
import com.dentalcare.app.data.repository.TreatmentRepository
import com.dentalcare.app.util.DateUtils
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val todayAppointments: Int = 0,
    val totalPatients: Int = 0,
    val pendingTreatments: Int = 0,
    val completedToday: Int = 0
)

data class DashboardState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val stats: DashboardStats = DashboardStats(),
    val todayAppointments: List<Appointment> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val treatmentRepository: TreatmentRepository
) : ViewModel() {
    
    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isLoading = true)
            
            // Load current user
            authRepository.getCurrentUser().collect { userResult ->
                when (userResult) {
                    is Resource.Success -> {
                        _dashboardState.value = _dashboardState.value.copy(
                            currentUser = userResult.data
                        )
                    }
                    else -> {}
                }
            }
            
            // Load appointments
            appointmentRepository.getAppointments().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val appointments = result.data ?: emptyList()
                        val today = DateUtils.getTodayString()
                        val todayAppointments = appointments.filter { 
                            DateUtils.isDateToday(it.date)
                        }
                        val completedToday = todayAppointments.count { 
                            it.status == com.dentalcare.app.data.model.AppointmentStatus.COMPLETED
                        }
                        
                        _dashboardState.value = _dashboardState.value.copy(
                            todayAppointments = todayAppointments,
                            stats = _dashboardState.value.stats.copy(
                                todayAppointments = todayAppointments.size,
                                completedToday = completedToday
                            )
                        )
                    }
                    is Resource.Error -> {
                        _dashboardState.value = _dashboardState.value.copy(
                            error = result.message ?: "Failed to load appointments"
                        )
                    }
                    else -> {}
                }
            }
            
            // Load patients count
            patientRepository.getPatients().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _dashboardState.value = _dashboardState.value.copy(
                            stats = _dashboardState.value.stats.copy(
                                totalPatients = result.data?.size ?: 0
                            )
                        )
                    }
                    else -> {}
                }
            }
            
            // Load treatments count
            treatmentRepository.getTreatments().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val pendingCount = result.data?.count { 
                            it.status != com.dentalcare.app.data.model.TreatmentStatus.COMPLETED &&
                            it.status != com.dentalcare.app.data.model.TreatmentStatus.CANCELLED
                        } ?: 0
                        
                        _dashboardState.value = _dashboardState.value.copy(
                            stats = _dashboardState.value.stats.copy(
                                pendingTreatments = pendingCount
                            ),
                            isLoading = false
                        )
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
}
