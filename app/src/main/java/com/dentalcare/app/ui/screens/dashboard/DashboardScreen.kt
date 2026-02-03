package com.dentalcare.app.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.data.model.Appointment
import com.dentalcare.app.ui.components.EmptyStateComponent
import com.dentalcare.app.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToPatients: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToTreatments: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddPatient: () -> Unit = {},
    onNavigateToAddAppointment: () -> Unit = {},
    onNavigateToAddTreatment: () -> Unit = {},
    onNavigateToAppointmentDetail: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Welcome back, ${dashboardState.currentUser?.name ?: "User"}!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            item {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedStatsCard(
                        title = "Today's\nAppointments",
                        value = dashboardState.stats.todayAppointments,
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                    AnimatedStatsCard(
                        title = "Total\nPatients",
                        value = dashboardState.stats.totalPatients,
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedStatsCard(
                        title = "Pending\nTreatments",
                        value = dashboardState.stats.pendingTreatments,
                        icon = Icons.Default.MedicalServices,
                        modifier = Modifier.weight(1f)
                    )
                    AnimatedStatsCard(
                        title = "Completed\nToday",
                        value = dashboardState.stats.completedToday,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionCard(
                            title = "Add Patient",
                            icon = Icons.Default.PersonAdd,
                            onClick = onNavigateToAddPatient
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Schedule",
                            icon = Icons.Default.EventAvailable,
                            onClick = onNavigateToAddAppointment
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "New Treatment",
                            icon = Icons.Default.MedicalServices,
                            onClick = onNavigateToAddTreatment
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "All Patients",
                            icon = Icons.Default.Person,
                            onClick = onNavigateToPatients
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Appointments",
                            icon = Icons.Default.DateRange,
                            onClick = onNavigateToAppointments
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Treatments",
                            icon = Icons.Default.MedicalServices,
                            onClick = onNavigateToTreatments
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Diagnostics",
                            icon = Icons.Default.Science,
                            onClick = onNavigateToDiagnostics
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Reports",
                            icon = Icons.Default.Assessment,
                            onClick = onNavigateToReports
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Today's Appointments",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            if (dashboardState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
            } else if (dashboardState.todayAppointments.isEmpty()) {
                item {
                    EmptyStateComponent(
                        icon = Icons.Default.EventBusy,
                        message = "No appointments scheduled for today"
                    )
                }
            } else {
                items(dashboardState.todayAppointments) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onClick = { onNavigateToAppointmentDetail(appointment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedStatsCard(
    title: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableStateOf(0) }
    
    LaunchedEffect(value) {
        val step = if (value > 0) value / 20 else 1
        for (i in 0..value step step.coerceAtLeast(1)) {
            animatedValue = i
            kotlinx.coroutines.delay(20)
        }
        animatedValue = value
    }
    
    val scale by animateFloatAsState(
        targetValue = if (animatedValue > 0) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = modifier.scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = animatedValue.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
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
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.patientName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${appointment.time} • ${appointment.serviceType}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Dr. ${appointment.doctorName}",
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

