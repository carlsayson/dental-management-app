package com.dentalcare.app.ui.screens.diagnostics

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.dentalcare.app.data.model.Diagnostic
import com.dentalcare.app.data.model.DiagnosticType
import com.dentalcare.app.ui.components.DropdownSelector
import com.dentalcare.app.ui.components.LoadingIndicator
import com.dentalcare.app.ui.screens.patients.PatientsViewModel
import com.dentalcare.app.data.repository.UserRepository
import com.dentalcare.app.util.DateUtils
import javax.inject.Inject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDiagnosticScreen(
    onNavigateBack: () -> Unit,
    diagnosticsViewModel: DiagnosticsViewModel = hiltViewModel(),
    patientsViewModel: PatientsViewModel = hiltViewModel(),
    userRepository: UserRepository = hiltViewModel<UserRepositoryHolder>().repository
) {
    var selectedPatient by remember { mutableStateOf<com.dentalcare.app.data.model.Patient?>(null) }
    var selectedDentist by remember { mutableStateOf<com.dentalcare.app.data.model.User?>(null) }
    var selectedType by remember { mutableStateOf(DiagnosticType.EXAMINATION) }
    var findings by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    var patientError by remember { mutableStateOf<String?>(null) }
    var dentistError by remember { mutableStateOf<String?>(null) }
    
    val patientsState by patientsViewModel.patientsState.collectAsState()
    val uploadState by diagnosticsViewModel.uploadState.collectAsState()
    val dentistsState = remember { mutableStateOf<List<com.dentalcare.app.data.model.User>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        userRepository.getDentists().collect { result ->
            if (result is com.dentalcare.app.util.Resource.Success) {
                dentistsState.value = result.data ?: emptyList()
            }
        }
    }
    
    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Diagnostic") },
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
                    error = patientError
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
                    label = "Diagnostic Type *",
                    items = DiagnosticType.values().toList(),
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it },
                    itemLabel = { it.name.replace("_", " ") }
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable {
                            // Simulated image picker
                            selectedImageUri = Uri.parse("content://sample/image.jpg")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tap to select image",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "(Simulated image picker)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                if (selectedImageUri != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                        OutlinedButton(
                            onClick = { 
                                // Simulate selecting another image
                                selectedImageUri = Uri.parse("content://sample/image${System.currentTimeMillis()}.jpg")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change")
                        }
                    }
                }
                
                OutlinedTextField(
                    value = findings,
                    onValueChange = { findings = it },
                    label = { Text("Findings") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Enter diagnostic findings") }
                )
                
                OutlinedTextField(
                    value = recommendations,
                    onValueChange = { recommendations = it },
                    label = { Text("Recommendations") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("Enter recommendations") }
                )
                
                if (uploadState.isLoading) {
                    LinearProgressIndicator(
                        progress = uploadState.progress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Uploading... ${uploadState.progress}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (uploadState.error.isNotEmpty()) {
                    Text(
                        text = uploadState.error,
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
                        
                        if (!hasError) {
                            val diagnostic = Diagnostic(
                                patientId = selectedPatient!!.id,
                                patientName = selectedPatient!!.getFullName(),
                                doctorId = selectedDentist!!.id,
                                doctorName = selectedDentist!!.name,
                                diagnosticType = selectedType,
                                findings = findings,
                                recommendations = recommendations,
                                date = DateUtils.getTodayString()
                            )
                            
                            scope.launch {
                                diagnosticsViewModel.uploadDiagnostic(diagnostic, selectedImageUri)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uploadState.isLoading
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Diagnostic")
                }
            }
            
            if (uploadState.isLoading) {
                LoadingIndicator()
            }
        }
    }
}

@dagger.hilt.android.lifecycle.HiltViewModel
class UserRepositoryHolder @Inject constructor(
    val repository: UserRepository
) : androidx.lifecycle.ViewModel()
