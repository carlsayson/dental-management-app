package com.dentalcare.app.util

import android.util.Patterns

object ValidationUtils {
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email is required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                ValidationResult(false, "Invalid email format")
            else -> ValidationResult(true)
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Password is required")
            password.length < Constants.MIN_PASSWORD_LENGTH -> 
                ValidationResult(false, "Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters")
            else -> ValidationResult(true)
        }
    }
    
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult(false, "Phone number is required")
            phone.length < 10 -> ValidationResult(false, "Invalid phone number")
            !phone.all { it.isDigit() || it in listOf('+', '-', ' ', '(', ')') } -> 
                ValidationResult(false, "Phone number contains invalid characters")
            else -> ValidationResult(true)
        }
    }
    
    fun validateRequired(value: String, fieldName: String = "This field"): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult(false, "$fieldName is required")
        } else {
            ValidationResult(true)
        }
    }
    
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name is required")
            name.length < 2 -> ValidationResult(false, "Name must be at least 2 characters")
            else -> ValidationResult(true)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
