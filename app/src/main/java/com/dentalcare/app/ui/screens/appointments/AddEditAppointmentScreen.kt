package com.dentalcare.app.ui.screens.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.data.model.Appointment
import com.dentalcare.app.data.model.AppointmentStatus
import com.dentalcare.app.data.model.ServiceTypes
import com.dentalcare.app.ui.components.DatePickerDialog
import com.dentalcare.app.ui.components.DropdownSelector
import com.dentalcare.app.ui.components.LoadingIndicator
import com.dentalcare.app.ui.components.TimePickerDialog
import com.dentalcare.app.ui.screens.patients.PatientsViewModel
import com.dentalcare.app.data.repository.UserRepository
import com.dentalcare.app.util.DateUtils
import com.dentalcare.app.util.ValidationUtils
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAppointmentScreen(
    appointmentId: String? = null,
    preselectedPatientId: String? = null,
    onNavigateBack: () -> Unit,
    appointmentViewModel: AppointmentsViewModel = hiltViewModel(),
    patientsViewModel: PatientsViewModel = hiltViewModel(),
    userRepository: UserRepository = hiltViewModel<UserRepositoryHolder>().repository
) {
    var selectedPatient by remember { mutableStateOf<com.dentalcare.app.data.model.Patient?>(null) }
    var selectedDentist by remember { mutableStateOf<com.dentalcare.app.data.model.User?>(null) }
    var selectedService by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(DateUtils.getTodayString()) }
    var selectedTime by remember { mutableStateOf("") }
    var isWalkIn by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    var patientError by remember { mutableStateOf<String?>(null) }
    var dentistError by remember { mutableStateOf<String?>(null) }
    var serviceError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }
    
    val patientsState by patientsViewModel.patientsState.collectAsState()
    val saveState by appointmentViewModel.saveState.collectAsState()
    val dentistsState = remember { mutableStateOf<List<com.dentalcare.app.data.model.User>>(emptyList()) }
    
    val isEditMode = appointmentId != null
    
    LaunchedEffect(Unit) {
        userRepository.getDentists().collect { result ->
            if (result is com.dentalcare.app.util.Resource.Success) {
                dentistsState.value = result.data ?: emptyList()
            }
        }
    }
    
    LaunchedEffect(preselectedPatientId, patientsState.patients) {
        if (preselectedPatientId != null) {
            selectedPatient = patientsState.patients.find { it.id == preselectedPatientId }
        }
    }
    
    LaunchedEffect(saveState.isSuccess) {
        if (saveState.isSuccess) {
            onNavigateBack()
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { 
                selectedDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = selectedDate
        )
    }
    
    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { 
                selectedTime = it
                timeError = null
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            initialTime = selectedTime
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Appointment" else "New Appointment") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DropdownSelector(
                    label = "Patient *",
                    items = patientsState.patients,
                    selectedItem = selectedPatient,
                    onItemSelected = { 
                        selectedPatient = it
                        patientError = null
                    },
                    itemLabel = { it.getFullName() },
                    searchable = true,
                    error = patientError,
                    enabled = preselectedPatientId == null
                )
                
                DropdownSelector(
                    label = "Dentist *",
                    items = dentistsState.value,
                    selectedItem = selectedDentist,
                    onItemSelected = { 
                        selectedDentist = it
                        dentistError = null
                    },
                    itemLabel = { "Dr. ${it.name}" },
                    error = dentistError
                )
                
                DropdownSelector(
                    label = "Service Type *",
                    items = ServiceTypes.ALL,
                    selectedItem = selectedService.takeIf { it.isNotEmpty() },
                    onItemSelected = { 
                        selectedService = it
                        serviceError = null
                    },
                    itemLabel = { it },
                    error = serviceError
                )
                
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    label = { Text("Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Pick date")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    label = { Text("Time *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Pick time")
                        }
                    },
                    isError = timeError != null,
                    supportingText = timeError?.let { { Text(it) } },
                    placeholder = { Text("Select appointment time") }
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Walk-in Patient", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isWalkIn,
                        onCheckedChange = { isWalkIn = it }
                    )
                }
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Additional notes (optional)") }
                )
                
                if (saveState.error.isNotEmpty()) {
                    Text(
                        text = saveState.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Button(
                    onClick = {
                        var hasError = false
                        
                        if (selectedPatient == null) {
                            patientError = "Please select a patient"
                            hasError = true
                        }
                        
                        if (selectedDentist == null) {
                            dentistError = "Please select a dentist"
                            hasError = true
                        }
                        
                        if (selectedService.isEmpty()) {
                            serviceError = "Please select a service type"
                            hasError = true
                        }
                        
                        if (selectedTime.isEmpty()) {
                            timeError = "Please select a time"
                            hasError = true
                        }
                        
                        if (!hasError) {
                            val appointment = Appointment(
                                id = appointmentId ?: "",
                                patientId = selectedPatient!!.id,
                                patientName = selectedPatient!!.getFullName(),
                                doctorId = selectedDentist!!.id,
                                doctorName = selectedDentist!!.name,
                                date = selectedDate,
                                time = selectedTime,
                                serviceType = selectedService,
                                reason = selectedService,
                                isWalkIn = isWalkIn,
                                notes = notes,
                                status = AppointmentStatus.SCHEDULED
                            )
                            appointmentViewModel.saveAppointment(appointment)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saveState.isLoading
                ) {
                    Text(if (isEditMode) "Update Appointment" else "Schedule Appointment")
                }
            }
            
            if (saveState.isLoading) {
                LoadingIndicator()
            }
        }
    }
}

@dagger.hilt.android.lifecycle.HiltViewModel
class UserRepositoryHolder @Inject constructor(
    val repository: UserRepository
) : androidx.lifecycle.ViewModel()
