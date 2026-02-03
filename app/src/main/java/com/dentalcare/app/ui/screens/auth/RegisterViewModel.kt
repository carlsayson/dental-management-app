package com.dentalcare.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.repository.AuthRepository
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()
    
    fun register(email: String, password: String, confirmPassword: String, name: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            _registerState.value = RegisterState(error = "Please fill all fields")
            return
        }
        
        if (password != confirmPassword) {
            _registerState.value = RegisterState(error = "Passwords do not match")
            return
        }
        
        if (password.length < 6) {
            _registerState.value = RegisterState(error = "Password must be at least 6 characters")
            return
        }
        
        viewModelScope.launch {
            authRepository.signUp(email, password, name).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _registerState.value = RegisterState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _registerState.value = RegisterState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _registerState.value = RegisterState(error = result.message ?: "Registration failed")
                    }
                }
            }
        }
    }
}
