package com.dentalcare.app.ui.screens.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.data.model.AppointmentStatus
import com.dentalcare.app.ui.components.ConfirmationDialog
import com.dentalcare.app.ui.components.LoadingIndicator
import com.dentalcare.app.data.repository.AppointmentRepository
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    appointmentId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPatient: (String) -> Unit = {},
    appointmentRepository: AppointmentRepository = hiltViewModel<AppointmentRepositoryHolder>().repository
) {
    var appointment by remember { mutableStateOf<com.dentalcare.app.data.model.Appointment?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(appointmentId) {
        appointmentRepository.getAppointment(appointmentId).collect { result ->
            when (result) {
                is com.dentalcare.app.util.Resource.Success -> {
                    appointment = result.data
                    isLoading = false
                }
                is com.dentalcare.app.util.Resource.Error -> {
                    isLoading = false
                }
                is com.dentalcare.app.util.Resource.Loading -> {
                    isLoading = true
                }
            }
        }
    }
    
    if (showConfirmDialog) {
        ConfirmationDialog(
            title = "Confirm Appointment",
            message = "Mark this appointment as confirmed?",
            onConfirm = {
                appointment?.let {
                    val updated = it.copy(status = AppointmentStatus.CONFIRMED)
                    kotlinx.coroutines.GlobalScope.launch {
                        appointmentRepository.updateAppointment(updated).collect {}
                    }
                }
                showConfirmDialog = false
                onNavigateBack()
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
    
    if (showCompleteDialog) {
        ConfirmationDialog(
            title = "Complete Appointment",
            message = "Mark this appointment as completed?",
            onConfirm = {
                appointment?.let {
                    val updated = it.copy(status = AppointmentStatus.COMPLETED)
                    kotlinx.coroutines.GlobalScope.launch {
                        appointmentRepository.updateAppointment(updated).collect {}
                    }
                }
                showCompleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showCompleteDialog = false }
        )
    }
    
    if (showCancelDialog) {
        ConfirmationDialog(
            title = "Cancel Appointment",
            message = "Are you sure you want to cancel this appointment?",
            onConfirm = {
                appointment?.let {
                    val updated = it.copy(status = AppointmentStatus.CANCELLED)
                    kotlinx.coroutines.GlobalScope.launch {
                        appointmentRepository.updateAppointment(updated).collect {}
                    }
                }
                showCancelDialog = false
                onNavigateBack()
            },
            onDismiss = { showCancelDialog = false },
            confirmText = "Cancel Appointment"
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else if (appointment == null) {
                Text(
                    "Appointment not found",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Patient Information", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow("Patient", appointment!!.patientName)
                            TextButton(onClick = { onNavigateToPatient(appointment!!.patientId) }) {
                                Text("View Patient Profile")
                            }
                        }
                    }
                    
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Appointment Details", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow("Dentist", "Dr. ${appointment!!.doctorName}")
                            DetailRow("Service", appointment!!.serviceType)
                            DetailRow("Date", appointment!!.date)
                            DetailRow("Time", appointment!!.time)
                            DetailRow("Status", appointment!!.status.name)
                            DetailRow("Type", if (appointment!!.isWalkIn) "Walk-in" else "Scheduled")
                        }
                    }
                    
                    if (appointment!!.notes.isNotEmpty()) {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Notes", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(appointment!!.notes, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    
                    if (appointment!!.status == AppointmentStatus.SCHEDULED) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm Appointment")
                        }
                    }
                    
                    if (appointment!!.status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)) {
                        Button(
                            onClick = { showCompleteDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark as Completed")
                        }
                    }
                    
                    if (appointment!!.status != AppointmentStatus.CANCELLED && 
                        appointment!!.status != AppointmentStatus.COMPLETED) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancel Appointment")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@dagger.hilt.android.lifecycle.HiltViewModel
class AppointmentRepositoryHolder @Inject constructor(
    val repository: AppointmentRepository
) : androidx.lifecycle.ViewModel()
