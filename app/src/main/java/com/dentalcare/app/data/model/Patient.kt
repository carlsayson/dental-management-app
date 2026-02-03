package com.dentalcare.app.data.model

data class Patient(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val medicalHistory: String = "",
    val dentalHistory: String = "",
    val allergies: List<String> = emptyList(),
    val bloodGroup: String = "",
    val emergencyContact: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getFullName(): String {
        return if (name.isNotEmpty()) {
            name
        } else {
            "$firstName $lastName".trim()
        }
    }
}
