package com.dentalcare.app.ui.screens.diagnostics

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
import com.dentalcare.app.data.model.Diagnostic
import com.dentalcare.app.ui.components.EmptyStateComponent
import com.dentalcare.app.ui.components.ErrorComponent
import com.dentalcare.app.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUpload: () -> Unit = {},
    onNavigateToViewer: (String, Int) -> Unit = { _, _ -> },
    viewModel: DiagnosticsViewModel = hiltViewModel()
) {
    val diagnosticsState by viewModel.diagnosticsState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToUpload) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Upload diagnostic")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                diagnosticsState.isLoading -> {
                    LoadingIndicator()
                }
                diagnosticsState.error.isNotEmpty() -> {
                    ErrorComponent(message = diagnosticsState.error)
                }
                diagnosticsState.diagnostics.isEmpty() -> {
                    EmptyStateComponent(
                        icon = Icons.Default.Image,
                        message = "No diagnostics found",
                        actionButton = {
                            Button(onClick = onNavigateToUpload) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload Diagnostic")
                            }
                        }
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(diagnosticsState.diagnostics) { diagnostic ->
                            DiagnosticCard(
                                diagnostic = diagnostic,
                                onClick = { onNavigateToViewer(diagnostic.id, 0) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticCard(
    diagnostic: Diagnostic,
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
                imageVector = Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = diagnostic.patientName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = diagnostic.diagnosticType.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Dr. ${diagnostic.doctorName} • ${diagnostic.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (diagnostic.images.isNotEmpty()) {
                Badge {
                    Text(diagnostic.images.size.toString())
                }
            }
        }
    }
}
