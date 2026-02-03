package com.dentalcare.app.ui.screens.diagnostics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.dentalcare.app.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticViewerScreen(
    diagnosticId: String,
    imageIndex: Int = 0,
    onNavigateBack: () -> Unit,
    viewModel: DiagnosticsViewModel = hiltViewModel()
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showDetails by remember { mutableStateOf(false) }
    
    val currentDiagnostic by viewModel.currentDiagnostic.collectAsState()
    
    LaunchedEffect(diagnosticId) {
        viewModel.loadDiagnostic(diagnosticId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostic Image") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDetails = !showDetails }) {
                        Icon(
                            if (showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle details"
                        )
                    }
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* Download functionality */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentDiagnostic == null) {
                LoadingIndicator()
            } else {
                val imageUrl = currentDiagnostic!!.images.getOrNull(imageIndex) ?: ""
                
                // Zoomable Image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                
                                if (scale > 1f) {
                                    val maxX = (size.width * (scale - 1)) / 2
                                    val maxY = (size.height * (scale - 1)) / 2
                                    offset = Offset(
                                        x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                        y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                                    )
                                } else {
                                    offset = Offset.Zero
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Diagnostic image",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                ),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Placeholder for when no image URL is available
                        Card(
                            modifier = Modifier
                                .size(300.dp)
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Sample Diagnostic Image",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "(Pinch to zoom)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Zoom indicator
                if (scale > 1f) {
                    Text(
                        text = "${(scale * 100).toInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                
                // Details overlay
                if (showDetails) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.8f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = currentDiagnostic!!.diagnosticType.name.replace("_", " "),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            
                            if (currentDiagnostic!!.patientName.isNotEmpty()) {
                                DetailRow("Patient", currentDiagnostic!!.patientName, Color.White)
                            }
                            
                            if (currentDiagnostic!!.doctorName.isNotEmpty()) {
                                DetailRow("Doctor", "Dr. ${currentDiagnostic!!.doctorName}", Color.White)
                            }
                            
                            DetailRow("Date", currentDiagnostic!!.date, Color.White)
                            
                            if (currentDiagnostic!!.findings.isNotEmpty()) {
                                Divider(color = Color.White.copy(alpha = 0.3f))
                                Text(
                                    text = "Findings:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = currentDiagnostic!!.findings,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                            
                            if (currentDiagnostic!!.recommendations.isNotEmpty()) {
                                Divider(color = Color.White.copy(alpha = 0.3f))
                                Text(
                                    text = "Recommendations:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = currentDiagnostic!!.recommendations,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                
                // Zoom controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .padding(bottom = if (showDetails) 200.dp else 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            scale = (scale + 0.5f).coerceAtMost(5f)
                        },
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in")
                    }
                    
                    FloatingActionButton(
                        onClick = {
                            if (scale > 1f) {
                                scale = (scale - 0.5f).coerceAtLeast(1f)
                                if (scale == 1f) {
                                    offset = Offset.Zero
                                }
                            }
                        },
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out")
                    }
                    
                    if (scale > 1f) {
                        FloatingActionButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.CenterFocusWeak, contentDescription = "Reset zoom")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}
