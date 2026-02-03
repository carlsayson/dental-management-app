package com.dentalcare.app.ui.screens.treatments

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
import com.dentalcare.app.ui.components.ConfirmationDialog
import com.dentalcare.app.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentDetailScreen(
    treatmentId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPatient: (String) -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
    viewModel: TreatmentsViewModel = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val currentTreatment by viewModel.currentTreatment.collectAsState()
    val deleteState by viewModel.saveState.collectAsState()
    
    LaunchedEffect(treatmentId) {
        viewModel.loadTreatment(treatmentId)
    }
    
    LaunchedEffect(deleteState.isSuccess) {
        if (deleteState.isSuccess) {
            onNavigateBack()
        }
    }
    
    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Delete Treatment",
            message = "Are you sure you want to delete this treatment record?",
            onConfirm = {
                viewModel.deleteTreatment(treatmentId)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            confirmText = "Delete"
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Treatment Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(treatmentId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
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
            if (currentTreatment == null) {
                LoadingIndicator()
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
                            DetailRow("Patient", currentTreatment!!.patientName)
                            TextButton(onClick = { onNavigateToPatient(currentTreatment!!.patientId) }) {
                                Text("View Patient Profile")
                            }
                        }
                    }
                    
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Treatment Information", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow("Type", currentTreatment!!.treatmentType)
                            DetailRow("Dentist", "Dr. ${currentTreatment!!.doctorName}")
                            DetailRow("Date", currentTreatment!!.startDate)
                            DetailRow("Status", currentTreatment!!.status.name)
                            if (currentTreatment!!.toothNumber.isNotEmpty()) {
                                DetailRow("Tooth Number", currentTreatment!!.toothNumber)
                            }
                            if (currentTreatment!!.cost > 0) {
                                DetailRow("Cost", "$${currentTreatment!!.cost}")
                                DetailRow("Paid", "$${currentTreatment!!.paid}")
                                DetailRow("Balance", "$${currentTreatment!!.cost - currentTreatment!!.paid}")
                            }
                        }
                    }
                    
                    if (currentTreatment!!.treatmentPlan.isNotEmpty() || currentTreatment!!.notes.isNotEmpty()) {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Treatment Notes", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentTreatment!!.notes.ifEmpty { currentTreatment!!.treatmentPlan },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    if (currentTreatment!!.medications.isNotEmpty()) {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Medications", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                currentTreatment!!.medications.forEach { med ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(med.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${med.dosage} - ${med.frequency} for ${med.duration}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    if (currentTreatment!!.endDate.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.EventNote, contentDescription = null)
                                Column {
                                    Text("Follow-up Required", style = MaterialTheme.typography.titleSmall)
                                    Text("Scheduled for: ${currentTreatment!!.endDate}", 
                                        style = MaterialTheme.typography.bodySmall)
                                }
                            }
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
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
