package com.dentalcare.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dentalcare.app.ui.screens.appointments.*
import com.dentalcare.app.ui.screens.auth.LoginScreen
import com.dentalcare.app.ui.screens.auth.RegisterScreen
import com.dentalcare.app.ui.screens.dashboard.DashboardScreen
import com.dentalcare.app.ui.screens.diagnostics.*
import com.dentalcare.app.ui.screens.patients.*
import com.dentalcare.app.ui.screens.reports.ReportsScreen
import com.dentalcare.app.ui.screens.settings.SettingsScreen
import com.dentalcare.app.ui.screens.splash.SplashScreen
import com.dentalcare.app.ui.screens.treatments.*

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
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
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAddPatient = { navController.navigate(Screen.AddPatient.route) },
                onNavigateToAddAppointment = { navController.navigate(Screen.AddAppointment.createRoute()) },
                onNavigateToAddTreatment = { navController.navigate(Screen.AddTreatment.createRoute()) },
                onNavigateToAppointmentDetail = { appointmentId ->
                    navController.navigate(Screen.AppointmentDetail.createRoute(appointmentId))
                }
            )
        }
        
        // Patients Routes
        composable(Screen.Patients.route) {
            PatientsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { patientId ->
                    navController.navigate(Screen.PatientDetail.createRoute(patientId))
                },
                onNavigateToAdd = { navController.navigate(Screen.AddPatient.route) }
            )
        }
        
        composable(Screen.AddPatient.route) {
            AddEditPatientScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditPatient.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            AddEditPatientScreen(
                patientId = patientId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.PatientDetail.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientDetailScreen(
                patientId = patientId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddAppointment = { pId ->
                    navController.navigate(Screen.AddAppointment.createRoute(pId))
                },
                onNavigateToAddTreatment = { pId ->
                    navController.navigate(Screen.AddTreatment.createRoute(pId))
                },
                onNavigateToEditPatient = { pId ->
                    navController.navigate(Screen.EditPatient.createRoute(pId))
                }
            )
        }
        
        // Appointments Routes
        composable(Screen.Appointments.route) {
            AppointmentsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.AddAppointment.createRoute()) },
                onNavigateToDetail = { appointmentId ->
                    navController.navigate(Screen.AppointmentDetail.createRoute(appointmentId))
                }
            )
        }
        
        composable(
            route = Screen.AddAppointment.route,
            arguments = listOf(
                navArgument("patientId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            AddEditAppointmentScreen(
                preselectedPatientId = patientId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditAppointment.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            AddEditAppointmentScreen(
                appointmentId = appointmentId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.AppointmentDetail.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            AppointmentDetailScreen(
                appointmentId = appointmentId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPatient = { patientId ->
                    navController.navigate(Screen.PatientDetail.createRoute(patientId))
                }
            )
        }
        
        // Treatments Routes
        composable(Screen.Treatments.route) {
            TreatmentsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.AddTreatment.createRoute()) },
                onNavigateToDetail = { treatmentId ->
                    navController.navigate(Screen.TreatmentDetail.createRoute(treatmentId))
                }
            )
        }
        
        composable(
            route = Screen.AddTreatment.route,
            arguments = listOf(
                navArgument("patientId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            AddEditTreatmentScreen(
                preselectedPatientId = patientId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditTreatment.route,
            arguments = listOf(navArgument("treatmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val treatmentId = backStackEntry.arguments?.getString("treatmentId") ?: ""
            AddEditTreatmentScreen(
                treatmentId = treatmentId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TreatmentDetail.route,
            arguments = listOf(navArgument("treatmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val treatmentId = backStackEntry.arguments?.getString("treatmentId") ?: ""
            TreatmentDetailScreen(
                treatmentId = treatmentId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPatient = { patientId ->
                    navController.navigate(Screen.PatientDetail.createRoute(patientId))
                },
                onNavigateToEdit = { tId ->
                    navController.navigate(Screen.EditTreatment.createRoute(tId))
                }
            )
        }
        
        // Diagnostics Routes
        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUpload = { navController.navigate(Screen.UploadDiagnostic.route) },
                onNavigateToViewer = { diagnosticId, imageIndex ->
                    navController.navigate(Screen.DiagnosticViewer.createRoute(diagnosticId, imageIndex))
                }
            )
        }
        
        composable(Screen.UploadDiagnostic.route) {
            UploadDiagnosticScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.DiagnosticViewer.route,
            arguments = listOf(
                navArgument("diagnosticId") { type = NavType.StringType },
                navArgument("imageIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val diagnosticId = backStackEntry.arguments?.getString("diagnosticId") ?: ""
            val imageIndex = backStackEntry.arguments?.getInt("imageIndex") ?: 0
            DiagnosticViewerScreen(
                diagnosticId = diagnosticId,
                imageIndex = imageIndex,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Reports and Settings
        composable(Screen.Reports.route) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
