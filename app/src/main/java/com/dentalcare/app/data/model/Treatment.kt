package com.dentalcare.app.data.model

data class Treatment(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val treatmentType: String = "",
    val treatmentPlan: String = "",
    val toothNumber: String = "",
    val cost: Double = 0.0,
    val paid: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val status: TreatmentStatus = TreatmentStatus.PLANNED,
    val notes: String = "",
    val medications: List<Medication> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Medication(
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val duration: String = ""
)

enum class TreatmentStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
