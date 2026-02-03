package com.dentalcare.app.data.model

data class Diagnostic(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val diagnosticType: DiagnosticType = DiagnosticType.EXAMINATION,
    val findings: String = "",
    val images: List<String> = emptyList(),
    val recommendations: String = "",
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DiagnosticType {
    XRAY,
    CT_SCAN,
    PANORAMIC,
    BITEWING,
    PERIAPICAL,
    EXAMINATION,
    OTHER
}
