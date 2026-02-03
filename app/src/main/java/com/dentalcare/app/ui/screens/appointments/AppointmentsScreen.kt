package com.dentalcare.app.ui.screens.appointments

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
import com.dentalcare.app.data.model.Appointment
import com.dentalcare.app.ui.components.EmptyStateComponent
import com.dentalcare.app.ui.components.ErrorComponent
import com.dentalcare.app.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: AppointmentsViewModel = hiltViewModel()
) {
    val appointmentsState by viewModel.appointmentsState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointments") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add appointment")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                appointmentsState.isLoading -> {
                    LoadingIndicator()
                }
                appointmentsState.error.isNotEmpty() -> {
                    ErrorComponent(message = appointmentsState.error)
                }
                appointmentsState.appointments.isEmpty() -> {
                    EmptyStateComponent(
                        icon = Icons.Default.CalendarToday,
                        message = "No appointments found",
                        actionButton = {
                            Button(onClick = onNavigateToAdd) {
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
                            AppointmentCard(
                                appointment = appointment,
                                onClick = { onNavigateToDetail(appointment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
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
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.patientName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${appointment.date} at ${appointment.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = appointment.serviceType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (appointment.status) {
                    com.dentalcare.app.data.model.AppointmentStatus.CONFIRMED -> 
                        MaterialTheme.colorScheme.primaryContainer
                    com.dentalcare.app.data.model.AppointmentStatus.COMPLETED -> 
                        MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
            ) {
                Text(
                    text = appointment.status.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
