package com.dentalcare.app.ui.screens.treatments

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
import com.dentalcare.app.data.model.Treatment
import com.dentalcare.app.data.model.TreatmentStatus
import com.dentalcare.app.ui.components.DatePickerDialog
import com.dentalcare.app.ui.components.DropdownSelector
import com.dentalcare.app.ui.components.LoadingIndicator
import com.dentalcare.app.ui.screens.patients.PatientsViewModel
import com.dentalcare.app.data.repository.UserRepository
import com.dentalcare.app.util.DateUtils
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTreatmentScreen(
    treatmentId: String? = null,
    preselectedPatientId: String? = null,
    onNavigateBack: () -> Unit,
    treatmentsViewModel: TreatmentsViewModel = hiltViewModel(),
    patientsViewModel: PatientsViewModel = hiltViewModel(),
    userRepository: UserRepository = hiltViewModel<UserRepositoryHolder>().repository
) {
    var selectedPatient by remember { mutableStateOf<com.dentalcare.app.data.model.Patient?>(null) }
    var selectedDentist by remember { mutableStateOf<com.dentalcare.app.data.model.User?>(null) }
    var treatmentType by remember { mutableStateOf("") }
    var treatmentDate by remember { mutableStateOf(DateUtils.getTodayString()) }
    var treatmentNotes by remember { mutableStateOf("") }
    var followUpRequired by remember { mutableStateOf(false) }
    var followUpDate by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showFollowUpDatePicker by remember { mutableStateOf(false) }
    
    var patientError by remember { mutableStateOf<String?>(null) }
    var dentistError by remember { mutableStateOf<String?>(null) }
    var treatmentTypeError by remember { mutableStateOf<String?>(null) }
    
    val patientsState by patientsViewModel.patientsState.collectAsState()
    val saveState by treatmentsViewModel.saveState.collectAsState()
    val dentistsState = remember { mutableStateOf<List<com.dentalcare.app.data.model.User>>(emptyList()) }
    
    val treatmentTypes = listOf(
        "Consultation",
        "Cleaning/Prophylaxis",
        "Tooth Extraction",
        "Root Canal Treatment",
        "Dental Filling",
        "Teeth Whitening",
        "Orthodontic Treatment",
        "Dental Implant",
        "Crown/Bridge Work",
        "Periodontal Treatment",
        "Emergency Care"
    )
    
    val isEditMode = treatmentId != null
    
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
    
    LaunchedEffect(treatmentId) {
        if (treatmentId != null) {
            treatmentsViewModel.loadTreatment(treatmentId)
        }
    }
    
    LaunchedEffect(treatmentsViewModel.currentTreatment.collectAsState().value) {
        treatmentsViewModel.currentTreatment.value?.let { treatment ->
            selectedPatient = patientsState.patients.find { it.id == treatment.patientId }
            selectedDentist = dentistsState.value.find { it.id == treatment.doctorId }
            treatmentType = treatment.treatmentType
            treatmentDate = treatment.startDate
            treatmentNotes = treatment.notes
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
                treatmentDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = treatmentDate
        )
    }
    
    if (showFollowUpDatePicker) {
        DatePickerDialog(
            onDateSelected = { 
                followUpDate = it
                showFollowUpDatePicker = false
            },
            onDismiss = { showFollowUpDatePicker = false },
            initialDate = followUpDate
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Treatment" else "New Treatment") },
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
                    label = "Treatment Type *",
                    items = treatmentTypes,
                    selectedItem = treatmentType.takeIf { it.isNotEmpty() },
                    onItemSelected = { 
                        treatmentType = it
                        treatmentTypeError = null
                    },
                    itemLabel = { it },
                    error = treatmentTypeError
                )
                
                OutlinedTextField(
                    value = treatmentDate,
                    onValueChange = {},
                    label = { Text("Treatment Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = treatmentNotes,
                    onValueChange = { treatmentNotes = it },
                    label = { Text("Treatment Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6,
                    placeholder = { Text("Enter detailed treatment notes") }
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Follow-up Required", modifier = Modifier.weight(1f))
                    Switch(
                        checked = followUpRequired,
                        onCheckedChange = { followUpRequired = it }
                    )
                }
                
                if (followUpRequired) {
                    OutlinedTextField(
                        value = followUpDate,
                        onValueChange = {},
                        label = { Text("Follow-up Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showFollowUpDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                            }
                        },
                        placeholder = { Text("Select follow-up date") }
                    )
                }
                
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
                        
                        if (treatmentType.isEmpty()) {
                            treatmentTypeError = "Please select a treatment type"
                            hasError = true
                        }
                        
                        if (!hasError) {
                            val treatment = Treatment(
                                id = treatmentId ?: "",
                                patientId = selectedPatient!!.id,
                                patientName = selectedPatient!!.getFullName(),
                                doctorId = selectedDentist!!.id,
                                doctorName = selectedDentist!!.name,
                                treatmentType = treatmentType,
                                treatmentPlan = treatmentNotes,
                                startDate = treatmentDate,
                                endDate = if (followUpRequired) followUpDate else "",
                                notes = treatmentNotes,
                                status = TreatmentStatus.PLANNED
                            )
                            treatmentsViewModel.saveTreatment(treatment)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saveState.isLoading
                ) {
                    Text(if (isEditMode) "Update Treatment" else "Save Treatment")
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
