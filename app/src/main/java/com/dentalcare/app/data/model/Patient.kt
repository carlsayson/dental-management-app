package com.dentalcare.app.data.model

data class Patient(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val medicalHistory: String = "",
    val allergies: List<String> = emptyList(),
    val bloodGroup: String = "",
    val emergencyContact: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
