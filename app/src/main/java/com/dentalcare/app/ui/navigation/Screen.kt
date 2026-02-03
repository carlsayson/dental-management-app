package com.dentalcare.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    
    object Patients : Screen("patients")
    object AddPatient : Screen("add_patient")
    object EditPatient : Screen("edit_patient/{patientId}") {
        fun createRoute(patientId: String) = "edit_patient/$patientId"
    }
    object PatientDetail : Screen("patient_detail/{patientId}") {
        fun createRoute(patientId: String) = "patient_detail/$patientId"
    }
    
    object Appointments : Screen("appointments")
    object AddAppointment : Screen("add_appointment?patientId={patientId}") {
        fun createRoute(patientId: String? = null) = 
            if (patientId != null) "add_appointment?patientId=$patientId" 
            else "add_appointment"
    }
    object EditAppointment : Screen("edit_appointment/{appointmentId}") {
        fun createRoute(appointmentId: String) = "edit_appointment/$appointmentId"
    }
    object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: String) = "appointment_detail/$appointmentId"
    }
    
    object Treatments : Screen("treatments")
    object AddTreatment : Screen("add_treatment?patientId={patientId}") {
        fun createRoute(patientId: String? = null) = 
            if (patientId != null) "add_treatment?patientId=$patientId" 
            else "add_treatment"
    }
    object EditTreatment : Screen("edit_treatment/{treatmentId}") {
        fun createRoute(treatmentId: String) = "edit_treatment/$treatmentId"
    }
    object TreatmentDetail : Screen("treatment_detail/{treatmentId}") {
        fun createRoute(treatmentId: String) = "treatment_detail/$treatmentId"
    }
    
    object Diagnostics : Screen("diagnostics")
    object UploadDiagnostic : Screen("upload_diagnostic")
    object DiagnosticViewer : Screen("diagnostic_viewer/{diagnosticId}/{imageIndex}") {
        fun createRoute(diagnosticId: String, imageIndex: Int = 0) = 
            "diagnostic_viewer/$diagnosticId/$imageIndex"
    }
    
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
