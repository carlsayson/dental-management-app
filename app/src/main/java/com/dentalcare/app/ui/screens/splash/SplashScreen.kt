package com.dentalcare.app.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dentalcare.app.ui.screens.auth.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    authViewModel: LoginViewModel = hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )
    
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        authViewModel.checkCurrentUser()
    }
    
    LaunchedEffect(currentUser) {
        if (currentUser.data != null) {
            onNavigateToDashboard()
        } else if (currentUser.data == null && !currentUser.message.isNullOrEmpty()) {
            onNavigateToLogin()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .graphicsLayer(alpha = alpha)
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = "Dental Care Logo",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Dental Care",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Management System",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}
