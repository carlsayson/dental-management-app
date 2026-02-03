package com.dentalcare.app.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.DENTIST,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    DENTIST,
    ASSISTANT,
    ADMIN
}
