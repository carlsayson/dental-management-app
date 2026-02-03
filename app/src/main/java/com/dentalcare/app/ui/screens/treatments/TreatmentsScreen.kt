package com.dentalcare.app.ui.screens.treatments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.data.model.Treatment
import com.dentalcare.app.ui.components.EmptyStateComponent
import com.dentalcare.app.ui.components.ErrorComponent
import com.dentalcare.app.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: TreatmentsViewModel = hiltViewModel()
) {
    val treatmentsState by viewModel.treatmentsState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Treatments") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add treatment")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                treatmentsState.isLoading -> {
                    LoadingIndicator()
                }
                treatmentsState.error.isNotEmpty() -> {
                    ErrorComponent(message = treatmentsState.error)
                }
                treatmentsState.treatments.isEmpty() -> {
                    EmptyStateComponent(
                        icon = Icons.Default.MedicalServices,
                        message = "No treatments found",
                        actionButton = {
                            Button(onClick = onNavigateToAdd) {
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
                            TreatmentCard(
                                treatment = treatment,
                                onClick = { onNavigateToDetail(treatment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TreatmentCard(
    treatment: Treatment,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = treatment.patientName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = treatment.treatmentType,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Dr. ${treatment.doctorName} • ${treatment.startDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (treatment.status) {
                    com.dentalcare.app.data.model.TreatmentStatus.COMPLETED -> 
                        MaterialTheme.colorScheme.tertiaryContainer
                    com.dentalcare.app.data.model.TreatmentStatus.IN_PROGRESS -> 
                        MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
            ) {
                Text(
                    text = treatment.status.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
