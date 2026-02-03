package com.dentalcare.app.ui.screens.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.data.model.Appointment
import com.dentalcare.app.data.model.Treatment
import com.dentalcare.app.ui.components.ConfirmationDialog
import com.dentalcare.app.ui.components.EmptyStateComponent
import com.dentalcare.app.ui.components.ErrorComponent
import com.dentalcare.app.ui.components.LoadingIndicator
import com.dentalcare.app.ui.screens.appointments.AppointmentsViewModel
import com.dentalcare.app.ui.screens.treatments.TreatmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    patientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAddAppointment: (String) -> Unit = {},
    onNavigateToAddTreatment: (String) -> Unit = {},
    onNavigateToEditPatient: (String) -> Unit = {},
    patientViewModel: PatientViewModel = hiltViewModel(),
    appointmentsViewModel: AppointmentsViewModel = hiltViewModel(),
    treatmentsViewModel: TreatmentsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val currentPatient by patientViewModel.currentPatient.collectAsState()
    val deleteState by patientViewModel.deleteState.collectAsState()
    
    LaunchedEffect(patientId) {
        patientViewModel.loadPatient(patientId)
        appointmentsViewModel.loadAppointmentsByPatient(patientId)
        treatmentsViewModel.loadTreatmentsByPatient(patientId)
    }
    
    LaunchedEffect(deleteState.isSuccess) {
        if (deleteState.isSuccess) {
            onNavigateBack()
        }
    }
    
    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Delete Patient",
            message = "Are you sure you want to delete this patient? This action cannot be undone.",
            onConfirm = {
                patientViewModel.deletePatient(patientId)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            confirmText = "Delete"
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentPatient?.getFullName() ?: "Patient Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEditPatient(patientId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Appointments") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Treatments") }
                )
            }
            
            when (selectedTab) {
                0 -> PatientOverviewTab(patient = currentPatient)
                1 -> PatientAppointmentsTab(
                    patientId = patientId,
                    onAddAppointment = { onNavigateToAddAppointment(patientId) },
                    viewModel = appointmentsViewModel
                )
                2 -> PatientTreatmentsTab(
                    patientId = patientId,
                    onAddTreatment = { onNavigateToAddTreatment(patientId) },
                    viewModel = treatmentsViewModel
                )
            }
        }
    }
}

@Composable
fun PatientOverviewTab(patient: com.dentalcare.app.data.model.Patient?) {
    if (patient == null) {
        LoadingIndicator()
        return
    }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Personal Information", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Name", patient.getFullName())
                    DetailRow("Phone", patient.phone.ifEmpty { "N/A" })
                    DetailRow("Address", patient.address.ifEmpty { "N/A" })
                }
            }
        }
        
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Medical History", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = patient.medicalHistory.ifEmpty { "No medical history recorded" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dental History", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = patient.dentalHistory.ifEmpty { "No dental history recorded" },
                        style = MaterialTheme.typography.bodyMedium
                    )
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
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PatientAppointmentsTab(
    patientId: String,
    onAddAppointment: () -> Unit,
    viewModel: AppointmentsViewModel
) {
    val appointmentsState by viewModel.appointmentsState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            appointmentsState.isLoading -> LoadingIndicator()
            appointmentsState.error.isNotEmpty() -> ErrorComponent(appointmentsState.error)
            appointmentsState.appointments.isEmpty() -> {
                EmptyStateComponent(
                    icon = Icons.Default.CalendarToday,
                    message = "No appointments found",
                    actionButton = {
                        Button(onClick = onAddAppointment) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Schedule Appointment")
                        }
                    }
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appointmentsState.appointments) { appointment ->
                        AppointmentCard(appointment)
                    }
                }
            }
        }
    }
}

@Composable
fun PatientTreatmentsTab(
    patientId: String,
    onAddTreatment: () -> Unit,
    viewModel: TreatmentsViewModel
) {
    val treatmentsState by viewModel.treatmentsState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            treatmentsState.isLoading -> LoadingIndicator()
            treatmentsState.error.isNotEmpty() -> ErrorComponent(treatmentsState.error)
            treatmentsState.treatments.isEmpty() -> {
                EmptyStateComponent(
                    icon = Icons.Default.MedicalServices,
                    message = "No treatments found",
                    actionButton = {
                        Button(onClick = onAddTreatment) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Treatment")
                        }
                    }
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(treatmentsState.treatments) { treatment ->
                        TreatmentCard(treatment)
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(appointment.doctorName, style = MaterialTheme.typography.titleMedium)
            Text("${appointment.date} at ${appointment.time}", style = MaterialTheme.typography.bodyMedium)
            Text(appointment.reason, style = MaterialTheme.typography.bodySmall)
            Text(
                text = appointment.status.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TreatmentCard(treatment: Treatment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(treatment.treatmentType, style = MaterialTheme.typography.titleMedium)
            Text("Dr. ${treatment.doctorName}", style = MaterialTheme.typography.bodyMedium)
            Text(treatment.startDate, style = MaterialTheme.typography.bodySmall)
            Text(
                text = treatment.status.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

