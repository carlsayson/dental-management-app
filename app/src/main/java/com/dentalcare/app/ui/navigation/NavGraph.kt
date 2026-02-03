package com.dentalcare.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dentalcare.app.ui.screens.appointments.AppointmentsScreen
import com.dentalcare.app.ui.screens.auth.LoginScreen
import com.dentalcare.app.ui.screens.auth.RegisterScreen
import com.dentalcare.app.ui.screens.dashboard.DashboardScreen
import com.dentalcare.app.ui.screens.diagnostics.DiagnosticsScreen
import com.dentalcare.app.ui.screens.patients.PatientDetailScreen
import com.dentalcare.app.ui.screens.patients.PatientsScreen
import com.dentalcare.app.ui.screens.reports.ReportsScreen
import com.dentalcare.app.ui.screens.settings.SettingsScreen
import com.dentalcare.app.ui.screens.treatments.TreatmentsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToPatients = { navController.navigate(Screen.Patients.route) },
                onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) },
                onNavigateToTreatments = { navController.navigate(Screen.Treatments.route) },
                onNavigateToDiagnostics = { navController.navigate(Screen.Diagnostics.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Patients.route) {
            PatientsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { patientId ->
                    navController.navigate(Screen.PatientDetail.createRoute(patientId))
                }
            )
        }
        
        composable(
            route = Screen.PatientDetail.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientDetailScreen(
                patientId = patientId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Appointments.route) {
            AppointmentsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Treatments.route) {
            TreatmentsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Reports.route) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
