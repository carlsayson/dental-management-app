package com.dentalcare.app.data.model

data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val date: String = "",
    val time: String = "",
    val serviceType: String = "",
    val reason: String = "",
    val isWalkIn: Boolean = false,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AppointmentStatus {
    SCHEDULED,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}

object ServiceTypes {
    val ALL = listOf(
        "Consultation",
        "Cleaning/Prophylaxis",
        "Tooth Extraction",
        "Root Canal Treatment",
        "Dental Filling",
        "Teeth Whitening",
        "Orthodontic Treatment",
        "Dental Implant",
        "Crown/Bridge Work"
    )
}
