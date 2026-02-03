package com.dentalcare.app.ui.screens.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.ui.components.LoadingIndicator
import com.dentalcare.app.util.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPatientScreen(
    patientId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: PatientViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var dentalHistory by remember { mutableStateOf("") }
    
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    val saveState by viewModel.saveState.collectAsState()
    val isEditMode = patientId != null
    
    LaunchedEffect(patientId) {
        if (patientId != null) {
            viewModel.loadPatient(patientId)
        }
    }
    
    LaunchedEffect(viewModel.currentPatient.collectAsState().value) {
        viewModel.currentPatient.value?.let { patient ->
            firstName = patient.firstName
            lastName = patient.lastName
            phone = patient.phone
            address = patient.address
            medicalHistory = patient.medicalHistory
            dentalHistory = patient.dentalHistory
        }
    }
    
    LaunchedEffect(saveState.isSuccess) {
        if (saveState.isSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Patient" else "Add Patient") },
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
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = null
                    },
                    label = { Text("First Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = firstNameError != null,
                    supportingText = firstNameError?.let { { Text(it) } },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = null
                    },
                    label = { Text("Last Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = lastNameError != null,
                    supportingText = lastNameError?.let { { Text(it) } },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = null
                    },
                    label = { Text("Contact Number") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError != null,
                    supportingText = phoneError?.let { { Text(it) } },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = medicalHistory,
                    onValueChange = { medicalHistory = it },
                    label = { Text("Medical History") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Enter patient's medical history (optional)") }
                )
                
                OutlinedTextField(
                    value = dentalHistory,
                    onValueChange = { dentalHistory = it },
                    label = { Text("Dental History") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Enter patient's dental history (optional)") }
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
                        
                        val firstNameValidation = ValidationUtils.validateRequired(firstName, "First name")
                        if (!firstNameValidation.isValid) {
                            firstNameError = firstNameValidation.errorMessage
                            hasError = true
                        }
                        
                        val lastNameValidation = ValidationUtils.validateRequired(lastName, "Last name")
                        if (!lastNameValidation.isValid) {
                            lastNameError = lastNameValidation.errorMessage
                            hasError = true
                        }
                        
                        if (phone.isNotEmpty()) {
                            val phoneValidation = ValidationUtils.validatePhone(phone)
                            if (!phoneValidation.isValid) {
                                phoneError = phoneValidation.errorMessage
                                hasError = true
                            }
                        }
                        
                        if (!hasError) {
                            viewModel.savePatient(
                                id = patientId ?: "",
                                firstName = firstName,
                                lastName = lastName,
                                phone = phone,
                                address = address,
                                medicalHistory = medicalHistory,
                                dentalHistory = dentalHistory
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saveState.isLoading
                ) {
                    Text(if (isEditMode) "Update Patient" else "Save Patient")
                }
            }
            
            if (saveState.isLoading) {
                LoadingIndicator()
            }
        }
    }
}
