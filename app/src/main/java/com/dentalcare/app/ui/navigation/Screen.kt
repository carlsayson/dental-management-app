package com.dentalcare.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Patients : Screen("patients")
    object PatientDetail : Screen("patient_detail/{patientId}") {
        fun createRoute(patientId: String) = "patient_detail/$patientId"
    }
    object Appointments : Screen("appointments")
    object Treatments : Screen("treatments")
    object Diagnostics : Screen("diagnostics")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
